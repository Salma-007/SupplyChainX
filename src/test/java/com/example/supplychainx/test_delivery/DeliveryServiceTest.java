package com.example.supplychainx.test_delivery;

import com.example.supplychainx.service_delivery.dto.delivery.DeliveryRequestDTO;
import com.example.supplychainx.service_delivery.dto.delivery.DeliveryResponseDTO;
import com.example.supplychainx.service_delivery.exceptions.DeliveryNotFoundException;
import com.example.supplychainx.service_delivery.exceptions.OrderNotFoundException;
import com.example.supplychainx.service_delivery.mapper.DeliveryMapper;
import com.example.supplychainx.service_delivery.model.Customer;
import com.example.supplychainx.service_delivery.model.Delivery;
import com.example.supplychainx.service_delivery.model.Order;
import com.example.supplychainx.service_delivery.model.enums.DeliveryStatus;
import com.example.supplychainx.service_delivery.repository.DeliveryRepository;
import com.example.supplychainx.service_delivery.repository.OrderRepository;
import com.example.supplychainx.service_delivery.service.DeliveryService;
import com.example.supplychainx.service_production.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private DeliveryMapper deliveryMapper;
    @Mock
    private Pageable pageable;

    @InjectMocks
    private DeliveryService deliveryService;

    private DeliveryRequestDTO deliveryRequestDTO;
    private Delivery delivery;
    private Order order;
    private Product product;
    private DeliveryResponseDTO deliveryResponseDTO;

    private final Long DELIVERY_ID = 1L;
    private final Long ORDER_ID = 10L;
    private final double DELIVERY_COST = 50.0;
    private final double PRODUCT_COST = 10.0;
    private final int QUANTITY = 5;
    private final Long CUSTOMER_ID = 99L;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setCost(PRODUCT_COST); // 10.0

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);

        order = new Order();
        order.setId(ORDER_ID);
        order.setProduct(product);
        order.setQuantity(QUANTITY); // 5
        order.setCustomer(customer);

        deliveryRequestDTO = new DeliveryRequestDTO(ORDER_ID, "Voiture","Taha", LocalDate.parse("2025-11-30"),DELIVERY_COST);

        delivery = new Delivery();
        delivery.setId(DELIVERY_ID);
        delivery.setOrder(order);
        delivery.setCost(DELIVERY_COST);
        delivery.setStatus(DeliveryStatus.PLANIFIEE);

        double totalCostExpected = (PRODUCT_COST * QUANTITY) + DELIVERY_COST; // 100.0

        deliveryResponseDTO = new DeliveryResponseDTO(DELIVERY_ID, ORDER_ID, order.getCustomer().getId(), "Voiture" ,DeliveryStatus.PLANIFIEE, "Ahmed", LocalDate.parse("2025-11-30"), 10.1, totalCostExpected);
        deliveryResponseDTO.setTotalCost(totalCostExpected);
    }

    // --- Tests pour createDelivery ---
    @Test
    void createDelivery_Success() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(deliveryRepository.existsByOrder_Id(ORDER_ID)).thenReturn(false);
        when(deliveryMapper.toEntity(deliveryRequestDTO)).thenReturn(delivery);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponseDTO partialResponseDTO = new DeliveryResponseDTO(DELIVERY_ID, ORDER_ID, order.getCustomer().getId(), "Voiture" ,DeliveryStatus.PLANIFIEE, "Ahmed", LocalDate.parse("2025-11-30"), 10.1, 100.0);
        when(deliveryMapper.toResponseDto(delivery)).thenReturn(partialResponseDTO);

        DeliveryResponseDTO result = deliveryService.createDelivery(deliveryRequestDTO);

        assertNotNull(result);
        assertEquals(DeliveryStatus.PLANIFIEE, delivery.getStatus());
        assertEquals(100.0, result.getTotalCost());

        verify(deliveryRepository).save(delivery);
    }

    @Test
    void createDelivery_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> deliveryService.createDelivery(deliveryRequestDTO));
        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void createDelivery_ThrowsRuntimeException_DeliveryAlreadyExists() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(deliveryRepository.existsByOrder_Id(ORDER_ID)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> deliveryService.createDelivery(deliveryRequestDTO));
        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void getAllDeliveries_Success() {
        Page<Delivery> deliveryPage = new PageImpl<>(Collections.singletonList(delivery));
        Page<DeliveryResponseDTO> expectedPage = new PageImpl<>(Collections.singletonList(deliveryResponseDTO));

        when(deliveryRepository.findAll(pageable)).thenReturn(deliveryPage);
        when(deliveryMapper.toResponseDto(delivery)).thenReturn(deliveryResponseDTO);

        Page<DeliveryResponseDTO> result = deliveryService.getAllDeliveries(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(deliveryResponseDTO.getId(), result.getContent().get(0).getId());
        verify(deliveryRepository).findAll(pageable);
    }

    @Test
    void getDeliveryById_Success() {
        when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
        when(deliveryMapper.toResponseDto(delivery)).thenReturn(deliveryResponseDTO);

        DeliveryResponseDTO result = deliveryService.getDeliveryById(DELIVERY_ID);

        assertNotNull(result);
        assertEquals(DELIVERY_ID, result.getId());
    }

    @Test
    void getDeliveryById_ThrowsDeliveryNotFoundException() {
        when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.getDeliveryById(DELIVERY_ID));
    }

    @Test
    void updateDelivery_Success() {
        Long NEW_ORDER_ID = 20L;
        Order newOrder = new Order();
        newOrder.setId(NEW_ORDER_ID);

        DeliveryRequestDTO newDto = new DeliveryRequestDTO(NEW_ORDER_ID, "Voiture","Taha", LocalDate.parse("2025-11-30"),75.0);
        Delivery updatedDelivery = new Delivery();
        updatedDelivery.setId(DELIVERY_ID);
        updatedDelivery.setOrder(newOrder);

        DeliveryResponseDTO expectedResponse = new DeliveryResponseDTO(DELIVERY_ID, NEW_ORDER_ID, order.getCustomer().getId(), "Voiture" ,DeliveryStatus.PLANIFIEE, "Ahmed", LocalDate.parse("2025-11-30"), 10.1, 75.0);

        when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
        when(orderRepository.findById(NEW_ORDER_ID)).thenReturn(Optional.of(newOrder));

        doNothing().when(deliveryMapper).updateDeliveryFromDto(newDto, delivery);

        when(deliveryRepository.save(delivery)).thenReturn(delivery);
        when(deliveryMapper.toResponseDto(delivery)).thenReturn(expectedResponse);

        DeliveryResponseDTO result = deliveryService.updateDelivery(DELIVERY_ID, newDto);

        assertNotNull(result);
        assertEquals(NEW_ORDER_ID, result.getOrderId());
        verify(deliveryMapper).updateDeliveryFromDto(newDto, delivery);
        verify(deliveryRepository).save(delivery);
    }

    @Test
    void updateDelivery_ThrowsDeliveryNotFoundException() {
        when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.updateDelivery(DELIVERY_ID, deliveryRequestDTO));
        verify(orderRepository, never()).findById(anyLong());
        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void updateDelivery_ThrowsOrderNotFoundException() {
        when(deliveryRepository.findById(DELIVERY_ID)).thenReturn(Optional.of(delivery));
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> deliveryService.updateDelivery(DELIVERY_ID, deliveryRequestDTO));
        verify(deliveryRepository, never()).save(any(Delivery.class));
    }

    @Test
    void deleteDelivery_Success() {
        when(deliveryRepository.existsById(DELIVERY_ID)).thenReturn(true);

        deliveryService.deleteDelivery(DELIVERY_ID);

        verify(deliveryRepository).existsById(DELIVERY_ID);
        verify(deliveryRepository).deleteById(DELIVERY_ID);
    }

    @Test
    void deleteDelivery_ThrowsDeliveryNotFoundException() {
        when(deliveryRepository.existsById(DELIVERY_ID)).thenReturn(false);

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.deleteDelivery(DELIVERY_ID));
        verify(deliveryRepository, never()).deleteById(anyLong());
    }
}