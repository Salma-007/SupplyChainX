package com.example.supplychainx.service_delivery.service;

import com.example.supplychainx.service_delivery.dto.delivery.DeliveryRequestDTO;
import com.example.supplychainx.service_delivery.dto.delivery.DeliveryResponseDTO;
import com.example.supplychainx.service_delivery.exceptions.DeliveryNotFoundException;
import com.example.supplychainx.service_delivery.exceptions.OrderNotFoundException;
import com.example.supplychainx.service_delivery.mapper.DeliveryMapper;
import com.example.supplychainx.service_delivery.model.Delivery;
import com.example.supplychainx.service_delivery.model.Order;
import com.example.supplychainx.service_delivery.model.enums.DeliveryStatus;
import com.example.supplychainx.service_delivery.repository.DeliveryRepository;
import com.example.supplychainx.service_delivery.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryMapper deliveryMapper;

    @Transactional
    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec id: " + dto.getOrderId()));

        if (deliveryRepository.existsByOrder_Id(dto.getOrderId())) {
            throw new RuntimeException("Une livraison existe déjà pour la commande avec id: " + dto.getOrderId());
        }

        Delivery delivery = deliveryMapper.toEntity(dto);
        delivery.setOrder(order);
        delivery.setStatus(DeliveryStatus.PLANIFIEE);


        Delivery saved = deliveryRepository.save(delivery);
        double totalCost = (saved.getOrder().getProduct().getCost() * saved.getOrder().getQuantity())+saved.getCost();
        DeliveryResponseDTO responseDTO = deliveryMapper.toResponseDto(saved);
        responseDTO.setTotalCost(totalCost);
        return responseDTO;
    }

    public Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable)
                .map(deliveryMapper::toResponseDto);
    }

    public DeliveryResponseDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException("Livraison non trouvée avec id: " + id));
        return deliveryMapper.toResponseDto(delivery);
    }

    @Transactional
    public DeliveryResponseDTO updateDelivery(Long id, DeliveryRequestDTO dto) {
        Delivery existingDelivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException("Livraison non trouvée avec id: " + id));

        Order newOrder = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec id: " + dto.getOrderId()));

        deliveryMapper.updateDeliveryFromDto(dto, existingDelivery);
        existingDelivery.setOrder(newOrder);

        Delivery updated = deliveryRepository.save(existingDelivery);
        return deliveryMapper.toResponseDto(updated);
    }

    @Transactional
    public void deleteDelivery(Long id) {
        if (!deliveryRepository.existsById(id)) {
            throw new DeliveryNotFoundException("Livraison non trouvée avec id: " + id);
        }
        deliveryRepository.deleteById(id);
    }
}
