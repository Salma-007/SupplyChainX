//package com.example.supplychainx.test_delivery;
//
//import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
//import com.example.supplychainx.service_approvisionnement.exceptions.InvalidOrderStatusException;
//import com.example.supplychainx.service_delivery.dto.order.OrderRequestDTO;
//import com.example.supplychainx.service_delivery.dto.order.OrderResponseDTO;
//import com.example.supplychainx.service_delivery.exceptions.CustomerNotFoundException;
//import com.example.supplychainx.service_delivery.exceptions.OrderNotFoundException;
//import com.example.supplychainx.service_delivery.mapper.OrderMapper;
//import com.example.supplychainx.service_delivery.model.Customer;
//import com.example.supplychainx.service_delivery.model.Order;
//import com.example.supplychainx.service_delivery.model.enums.OrderStatus;
//import com.example.supplychainx.service_delivery.repository.CustomerRepository;
//import com.example.supplychainx.service_delivery.repository.OrderRepository;
//import com.example.supplychainx.service_delivery.service.OrderService;
//import com.example.supplychainx.service_production.exceptions.ProductNotFoundException;
//import com.example.supplychainx.service_production.model.Product;
//import com.example.supplychainx.service_production.repository.ProductRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock
//    private OrderMapper orderMapper;
//    @Mock
//    private ProductRepository productRepository;
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    private OrderRequestDTO orderRequestDTO;
//    private Order order;
//    private OrderResponseDTO orderResponseDTO;
//    private Product product;
//    private Customer customer;
//    private final Long ORDER_ID = 1L;
//    private final Long PRODUCT_ID = 10L;
//    private final Long CUSTOMER_ID = 20L;
//
//    @BeforeEach
//    void setUp() {
//        orderRequestDTO = new OrderRequestDTO(PRODUCT_ID, CUSTOMER_ID, 5);
//
//        product = new Product();
//        product.setId(PRODUCT_ID);
//
//        customer = new Customer();
//        customer.setId(CUSTOMER_ID);
//
//        order = new Order();
//        order.setId(ORDER_ID);
//        order.setProduct(product);
//        order.setCustomer(customer);
//        order.setStatus(OrderStatus.EN_PREPARATION);
//
//        orderResponseDTO = new OrderResponseDTO(ORDER_ID, PRODUCT_ID, product.getName(), CUSTOMER_ID, customer.getName() ,5, OrderStatus.EN_PREPARATION);
//    }
//
//
//    @Test
//    void createOrder_Success() {
//        when(orderMapper.toEntity(orderRequestDTO)).thenReturn(order);
//        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
//        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//        when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDTO);
//
//        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);
//
//        assertNotNull(result);
//        assertEquals(OrderStatus.EN_PREPARATION, order.getStatus());
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void createOrder_ThrowsProductNotFoundException() {
//        when(orderMapper.toEntity(orderRequestDTO)).thenReturn(order);
//        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
//
//        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(orderRequestDTO));
//        verify(customerRepository, never()).findById(anyLong());
//    }
//
//    @Test
//    void createOrder_ThrowsCustomerNotFoundException() {
//        when(orderMapper.toEntity(orderRequestDTO)).thenReturn(order);
//        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
//        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(CustomerNotFoundException.class, () -> orderService.createOrder(orderRequestDTO));
//    }
//
//
//    @Test
//    void getAllOrders_Success() {
//        List<Order> orders = Arrays.asList(order, new Order());
//        List<OrderResponseDTO> expectedDTOs = Arrays.asList(orderResponseDTO, new OrderResponseDTO());
//
//        when(orderRepository.findAll()).thenReturn(orders);
//        when(orderMapper.toResponseDto(any(Order.class)))
//                .thenReturn(orderResponseDTO)
//                .thenReturn(new OrderResponseDTO());
//
//        List<OrderResponseDTO> result = orderService.getAllOrders();
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        verify(orderRepository).findAll();
//    }
//
//
//    @Test
//    void getOrderById_Success() {
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//        when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDTO);
//
//        OrderResponseDTO result = orderService.getOrderById(ORDER_ID);
//
//        assertNotNull(result);
//        assertEquals(ORDER_ID, result.getId());
//    }
//
//    @Test
//    void getOrderById_ThrowsOrderNotFoundException() {
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(ORDER_ID));
//    }
//
//
//    @Test
//    void deleteOrder_Success() {
//        when(orderRepository.existsById(ORDER_ID)).thenReturn(true);
//
//        orderService.deleteOrder(ORDER_ID);
//
//        verify(orderRepository).existsById(ORDER_ID);
//        verify(orderRepository).deleteById(ORDER_ID);
//    }
//
//    @Test
//    void deleteOrder_ThrowsOrderNotFoundException() {
//        when(orderRepository.existsById(ORDER_ID)).thenReturn(false);
//
//        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(ORDER_ID));
//        verify(orderRepository, never()).deleteById(anyLong());
//    }
//
//
//    @Test
//    void updateOrder_Success() {
//        Order updatedOrder = new Order();
//        updatedOrder.setId(ORDER_ID);
//        OrderRequestDTO newDto = new OrderRequestDTO(PRODUCT_ID, CUSTOMER_ID, 10);
//        OrderResponseDTO expectedResponse = new OrderResponseDTO(ORDER_ID, PRODUCT_ID, product.getName(), CUSTOMER_ID, customer.getName() ,10, OrderStatus.EN_PREPARATION);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//        when(orderMapper.toEntity(newDto)).thenReturn(updatedOrder);
//        when(orderRepository.save(updatedOrder)).thenReturn(updatedOrder);
//        when(orderMapper.toResponseDto(updatedOrder)).thenReturn(expectedResponse);
//
//        OrderResponseDTO result = orderService.updateOrder(ORDER_ID, newDto);
//
//        assertNotNull(result);
//        assertEquals(ORDER_ID, updatedOrder.getId());
//        assertEquals(10, result.getQuantity());
//        verify(orderRepository).save(updatedOrder);
//    }
//
//    @Test
//    void updateOrder_ThrowsOrderNotFoundException() {
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(ORDER_ID, orderRequestDTO));
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//
//
//    @Test
//    void annulerOrder_Success_InitialStatusNonLIVREE_NonENROUTE() {
//        order.setStatus(OrderStatus.EN_PREPARATION);
//        Order updatedOrder = new Order();
//        updatedOrder.setId(ORDER_ID);
//        updatedOrder.setStatus(OrderStatus.ANNULEE);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
//        when(orderMapper.toResponseDto(updatedOrder)).thenReturn(new OrderResponseDTO(ORDER_ID, PRODUCT_ID, product.getName(), CUSTOMER_ID, customer.getName() ,5, OrderStatus.ANNULEE));
//
//        OrderResponseDTO result = orderService.annulerOrder(ORDER_ID);
//
//        assertEquals(OrderStatus.ANNULEE, result.getStatus());
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void annulerOrder_ThrowsOrderNotFoundException() {
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(OrderNotFoundException.class, () -> orderService.annulerOrder(ORDER_ID));
//    }
//
//    @Test
//    void annulerOrder_ThrowsBusinessException_StatusLIVREE() {
//        order.setStatus(OrderStatus.LIVREE);
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        assertThrows(BusinessException.class, () -> orderService.annulerOrder(ORDER_ID));
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//
//    @Test
//    void annulerOrder_ThrowsBusinessException_StatusENROUTE() {
//        order.setStatus(OrderStatus.EN_ROUTE);
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        assertThrows(BusinessException.class, () -> orderService.annulerOrder(ORDER_ID));
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//
//    @Test
//    void updateStatus_Success() {
//        order.setStatus(OrderStatus.LIVREE);
//        String newStatusString = OrderStatus.LIVREE.toString();
//
//        Order updatedOrder = new Order();
//        updatedOrder.setId(ORDER_ID);
//        updatedOrder.setStatus(OrderStatus.LIVREE);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
//        when(orderMapper.toResponseDto(updatedOrder)).thenReturn(new OrderResponseDTO(ORDER_ID, PRODUCT_ID, product.getName(), CUSTOMER_ID, customer.getName() ,5, OrderStatus.LIVREE));
//
//        OrderResponseDTO result = orderService.updateStatus(ORDER_ID, newStatusString);
//
//        assertEquals(OrderStatus.LIVREE, result.getStatus());
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void updateStatus_ThrowsOrderNotFoundException() {
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(OrderNotFoundException.class, () -> orderService.updateStatus(ORDER_ID, "EN_ROUTE"));
//    }
//
//    @Test
//    void updateStatus_ThrowsInvalidOrderStatusException() {
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateStatus(ORDER_ID, "STATUT_INVALIDE"));
//    }
//
//    @Test
//    void updateStatus_ThrowsBusinessException_StatusNotLIVREE() {
//        order.setStatus(OrderStatus.EN_PREPARATION);
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        assertThrows(BusinessException.class, () -> orderService.updateStatus(ORDER_ID, OrderStatus.EN_ROUTE.toString()));
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//}
