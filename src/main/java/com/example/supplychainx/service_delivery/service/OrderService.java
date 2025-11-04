package com.example.supplychainx.service_delivery.service;

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
import com.example.supplychainx.service_production.exceptions.ProductNotFoundException;
import com.example.supplychainx.service_production.model.Product;
import com.example.supplychainx.service_production.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

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
}
