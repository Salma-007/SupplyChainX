package com.example.supplychainx.service_approvisionnement.service;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderRequestDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.*;
import com.example.supplychainx.service_approvisionnement.mapper.SupplyOrderItemMapper;
import com.example.supplychainx.service_approvisionnement.mapper.SupplyOrderMapper;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrder;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrderItem;
import com.example.supplychainx.service_approvisionnement.model.enums.SupplyOrderStatus;
import com.example.supplychainx.service_approvisionnement.repository.RawMaterialRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplierRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplyOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyOrderService {
    private final SupplyOrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplyOrderMapper orderMapper;
    private final SupplyOrderItemMapper itemMapper;

    @Transactional
    public SupplyOrder createOrder(SupplyOrderRequestDTO orderDTO) {

        SupplyOrder order = orderMapper.toEntity(orderDTO);
        order.setOrderdate(LocalDate.now());
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);

        order.setSupplier(supplierRepository.findById(orderDTO.getSupplierId())
                .orElseThrow(() -> new SupplierNotFoundException("Fournisseur non trouvé avec l'ID: " + orderDTO.getSupplierId())));

        List<SupplyOrderItem> items = orderDTO.getOrderItems().stream()
                .map(itemDTO -> {
                    SupplyOrderItem item = itemMapper.toEntity(itemDTO);
                    item.setSupplyOrder(order);

                    item.setRawMaterial(rawMaterialRepository.findById(itemDTO.getMaterialId())
                            .orElseThrow(() -> new RawMaterialNotFoundException("Matière Première non trouvée avec l'ID: " + itemDTO.getMaterialId())));
                    return item;
                })
                .collect(Collectors.toList());

        order.setOrderItems(items);
        return orderRepository.save(order);
    }

    public SupplyOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new SupplyOrderNotFoundException("Commande non trouvée avec l'ID: " + id));
    }

    public List<SupplyOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public SupplyOrder updateOrderStatus(Long orderId, String newStatusString) {
        SupplyOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new SupplyOrderNotFoundException("Commande non trouvée avec l'ID: " + orderId));

        SupplyOrderStatus newStatus;
        try {
            newStatus = SupplyOrderStatus.valueOf(newStatusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Le statut fourni est invalide: " + newStatusString);
        }

        if (order.getStatus() == SupplyOrderStatus.RECUE) {
            throw new OrderIsReceivedException("Impossible de modifier le statut d'une commande déjà annulée.");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        SupplyOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new SupplyOrderNotFoundException("Commande non trouvée avec l'ID: " + orderId));

        if (order.getStatus() == SupplyOrderStatus.RECUE) {
            throw new OrderIsReceivedException("Impossible de supprimer une commande déjà LIVREE.");
        }
        orderRepository.delete(order);
    }
}
