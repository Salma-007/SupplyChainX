package com.example.supplychainx.service_production.service;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem.SupplyOrderItemDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_approvisionnement.exceptions.InvalidOrderStatusException;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_approvisionnement.repository.RawMaterialRepository;
import com.example.supplychainx.service_approvisionnement.service.SupplyOrderService;
import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderRequestDTO;
import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderResponseDTO;
import com.example.supplychainx.service_production.exceptions.ProductNotFoundException;
import com.example.supplychainx.service_production.exceptions.ProductionOrderNotFoundException;
import com.example.supplychainx.service_production.mapper.ProductionOrderMapper;
import com.example.supplychainx.service_production.model.BillOfMaterial;
import com.example.supplychainx.service_production.model.Product;
import com.example.supplychainx.service_production.model.ProductionOrder;
import com.example.supplychainx.service_production.model.enums.ProductionOrderStatus;
import com.example.supplychainx.service_production.repository.BillOfMaterialRepository;
import com.example.supplychainx.service_production.repository.ProductRepository;
import com.example.supplychainx.service_production.repository.ProductionOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionOrderService {
    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final ProductionOrderMapper productionOrderMapper;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplyOrderService supplyOrderService;
    private final BillOfMaterialRepository billOfMaterialRepository;

    @Transactional
    public ProductionOrderResponseDTO addProductionOrder(ProductionOrderRequestDTO dto){
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec id: " + dto.getProductId()));

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException("La date de début ne peut pas être après la date de fin.");
        }

        ProductionOrder order = productionOrderMapper.toEntity(dto);
        order.setProduct(product);
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);
        ProductionOrder saved = productionOrderRepository.save(order);
        return productionOrderMapper.toResponseDto(saved);
    }

    public Page<ProductionOrderResponseDTO> getAllProductionOrders(Pageable pageable){
        Page<ProductionOrder> orderPage = productionOrderRepository.findAll(pageable);
        return orderPage.map(productionOrderMapper::toResponseDto);
    }

    public ProductionOrderResponseDTO getProductionOrderById(Long id){
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ProductionOrderNotFoundException("Ordre de production non trouvé avec id: " + id));
        return productionOrderMapper.toResponseDto(order);
    }

    @Transactional
    public ProductionOrderResponseDTO updateProductionOrder(Long id, ProductionOrderRequestDTO dto){
        ProductionOrder existingOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ProductionOrderNotFoundException("Ordre de production non trouvé avec id: " + id));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec id: " + dto.getProductId()));

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException("La date de début ne peut pas être après la date de fin.");
        }

        productionOrderMapper.updateEntityFromDto(dto, existingOrder);
        existingOrder.setProduct(product);

        ProductionOrder updated = productionOrderRepository.save(existingOrder);
        return productionOrderMapper.toResponseDto(updated);
    }

    public void deleteProductionOrder(Long id){
        if(!productionOrderRepository.existsById(id)){
            throw new ProductionOrderNotFoundException("Ordre de production non trouvé avec id: " + id);
        }
        productionOrderRepository.deleteById(id);
    }

    public Long getEstimatedProductionTime(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ProductionOrderNotFoundException("Ordre de production non trouvé avec id: " + id));

        Product product = order.getProduct();
        if (product == null) {
            throw new ProductNotFoundException("Le produit associé à l'ordre de production " + id + " est introuvable.");
        }

        Long totalTime = (long) order.getQuantity() * product.getProductionTime();

        return totalTime;
    }

    @Transactional
    public ProductionOrderResponseDTO blockProductionOrder(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ProductionOrderNotFoundException("Ordre de production non trouvé avec id: " + id));

        if (order.getStatus() != ProductionOrderStatus.EN_ATTENTE) {
            throw new BusinessException(
                    "Impossible de bloquer l'ordre de production " + id + ". Son statut actuel est: " + order.getStatus() +
                            ". Le blocage n'est autorisé que pour le statut " + ProductionOrderStatus.EN_ATTENTE
            );
        }
        order.setStatus(ProductionOrderStatus.BLOQUE);
        ProductionOrder updated = productionOrderRepository.save(order);
        return productionOrderMapper.toResponseDto(updated);
    }


    @Transactional
    public ProductionOrderResponseDTO updateStatus(Long id, String newStatus){
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new ProductionOrderNotFoundException("Ordre de production non trouvé avec id: " + id));

        ProductionOrderStatus status;
        try{
            status = ProductionOrderStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Le statut fourni est invalide: " + newStatus);
        }

        if (order.getStatus() == ProductionOrderStatus.TERMINE) {
            throw new BusinessException("Impossible de modifier le statut d'un ordre de production déjà TERMINÉ.");
        }

        if (status == ProductionOrderStatus.EN_PRODUCTION && order.getStatus() != ProductionOrderStatus.EN_PRODUCTION) {

            Product product = order.getProduct();
            int productionQuantity = order.getQuantity();

            List<BillOfMaterial> bomList = billOfMaterialRepository.findByProductId(product.getId());

            for (BillOfMaterial bomItem : bomList) {

                RawMaterial rawMaterial = bomItem.getRawMaterial();
                int requiredQuantity = bomItem.getQuantity() * productionQuantity;
                int currentStock = rawMaterial.getStock();

                if (currentStock < requiredQuantity) {

                    int quantityToOrder = requiredQuantity - currentStock;

                    SupplyOrderItemDTO itemDTO = new SupplyOrderItemDTO(
                            rawMaterial.getId(),
                            quantityToOrder
                    );

                    SupplyOrderRequestDTO supplyDTO = new SupplyOrderRequestDTO(
                            1L,
                            List.of(itemDTO)
                    );

                    supplyOrderService.createOrder(supplyDTO);

                    order.setStatus(ProductionOrderStatus.EN_ATTENTE);
                    productionOrderRepository.save(order);

                    throw new BusinessException(
                            "Matière première insuffisante: " + rawMaterial.getName() +
                                    ". Requis: " + requiredQuantity + ". Un ordre fournisseur a été lancé. Production maintenue à EN_ATTENTE."
                    );
                }
            }

            for (BillOfMaterial bomItem : bomList) {
                RawMaterial rawMaterial = bomItem.getRawMaterial();
                int requiredQuantity = bomItem.getQuantity() * productionQuantity;

                rawMaterial.setStock(rawMaterial.getStock() - requiredQuantity);
                rawMaterialRepository.save(rawMaterial);
            }

        }

        if(status == ProductionOrderStatus.TERMINE && order.getStatus() != ProductionOrderStatus.TERMINE){
            Product product = order.getProduct();
            int current = product.getStock();
            int ordered = order.getQuantity();
            product.setStock(current + ordered);
            productRepository.save(product);
        }

        order.setStatus(status);
        ProductionOrder updated = productionOrderRepository.save(order);
        return productionOrderMapper.toResponseDto(updated);
    }
}
