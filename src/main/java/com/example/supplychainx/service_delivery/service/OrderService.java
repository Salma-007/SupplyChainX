package com.example.supplychainx.service_delivery.service;

import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_approvisionnement.exceptions.InvalidOrderStatusException;
import com.example.supplychainx.service_delivery.dto.order.OrderRequestDTO;
import com.example.supplychainx.service_delivery.dto.order.OrderResponseDTO;
import com.example.supplychainx.service_delivery.exceptions.CustomerNotFoundException;
import com.example.supplychainx.service_delivery.exceptions.OrderNotFoundException;
import com.example.supplychainx.service_delivery.mapper.OrderMapper;
import com.example.supplychainx.service_delivery.model.Customer;
import com.example.supplychainx.service_delivery.model.Order;
import com.example.supplychainx.service_delivery.model.enums.DeliveryStatus;
import com.example.supplychainx.service_delivery.model.enums.OrderStatus;
import com.example.supplychainx.service_delivery.repository.CustomerRepository;
import com.example.supplychainx.service_delivery.repository.OrderRepository;
import com.example.supplychainx.service_production.dto.product.ProductResponseDTO;
import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderRequestDTO;
import com.example.supplychainx.service_production.exceptions.ProductNotFoundException;
import com.example.supplychainx.service_production.model.Product;
import com.example.supplychainx.service_production.model.enums.ProductionOrderStatus;
import com.example.supplychainx.service_production.repository.ProductRepository;
import com.example.supplychainx.service_production.service.ProductService;
import com.example.supplychainx.service_production.service.ProductionOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CustomerRepository customerRepository;
    private final ProductionOrderService productionOrderService;

    public OrderResponseDTO createOrder(OrderRequestDTO dto){
        Order order = orderMapper.toEntity(dto);
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec id: " + dto.getProductId()));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Client non trouvé avec id: " + dto.getCustomerId()));

        order.setProduct(product);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.EN_PREPARATION);
        Order saved = orderRepository.save(order);
        return orderMapper.toResponseDto(saved);
    }

    public List<OrderResponseDTO> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec id: " + id));

        return orderMapper.toResponseDto(order);
    }

    public void deleteOrder(Long id){
        if(!orderRepository.existsById(id)){
            throw new OrderNotFoundException("Commande non trouvée avec id: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto){
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec id: " + id));

        Order updatedOrder = orderMapper.toEntity(dto);
        updatedOrder.setId(id);

        Order saved = orderRepository.save(updatedOrder);
        return orderMapper.toResponseDto(saved);
    }

    public OrderResponseDTO annulerOrder(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Ordre de production non trouvé avec id: " + id));
        if(order.getStatus() == OrderStatus.LIVREE || order.getStatus() == OrderStatus.EN_ROUTE){
            throw new BusinessException(
                    "Impossible de bloquer l'ordre " + id + ". Son statut actuel est: " + order.getStatus() +
                            ". Le blocage n'est autorisé que pour le statut " + OrderStatus.EN_PREPARATION
            );
        }
        order.setStatus(OrderStatus.ANNULEE);
        Order updated = orderRepository.save(order);
        return orderMapper.toResponseDto(updated);
    }


    @Transactional
    public OrderResponseDTO updateStatus(Long id, String status){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec id: " + id));

        OrderStatus newStatus;
        try{
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Le statut fourni est invalide: " + status);
        }

        if (order.getStatus() == OrderStatus.LIVREE) {
            throw new BusinessException("Impossible de modifier le statut d'une commande déjà LIVREE.");
        }

        if (newStatus == OrderStatus.EN_ROUTE && order.getStatus() != OrderStatus.EN_ROUTE) {

            Product product = order.getProduct();
            int requestedQuantity = order.getQuantity();
            int currentStock = product.getStock();

            if (currentStock < requestedQuantity) {

                int quantityToProduce = requestedQuantity - currentStock;

                ProductionOrderRequestDTO productionDTO = new ProductionOrderRequestDTO(
                        quantityToProduce,
                        ProductionOrderStatus.EN_ATTENTE,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(7),
                        product.getId()
                );

                productionOrderService.addProductionOrder(productionDTO);

                order.setStatus(OrderStatus.EN_PREPARATION);
                orderRepository.save(order);

                throw new BusinessException(
                        "Stock insuffisant pour le produit " + product.getName() +
                                ". Un ordre de production de " + quantityToProduce +
                                " unités a été automatiquement lancé. La commande client est passée au statut 'EN_PREPARATION'."
                );
            }

            product.setStock(currentStock - requestedQuantity);
            productRepository.save(product);
        }

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return orderMapper.toResponseDto(updated);
    }
}
