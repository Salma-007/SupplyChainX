package com.example.supplychainx.test_production;

import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_approvisionnement.exceptions.InvalidOrderStatusException;
import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderRequestDTO;
import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderResponseDTO;
import com.example.supplychainx.service_production.exceptions.ProductNotFoundException;
import com.example.supplychainx.service_production.exceptions.ProductionOrderNotFoundException;
import com.example.supplychainx.service_production.mapper.ProductionOrderMapper;
import com.example.supplychainx.service_production.model.Product;
import com.example.supplychainx.service_production.model.ProductionOrder;
import com.example.supplychainx.service_production.model.enums.ProductionOrderStatus;
import com.example.supplychainx.service_production.repository.BillOfMaterialRepository;
import com.example.supplychainx.service_production.repository.ProductRepository;
import com.example.supplychainx.service_production.repository.ProductionOrderRepository;
import com.example.supplychainx.service_production.service.ProductionOrderService;
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
class ProductionOrderServiceTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BillOfMaterialRepository billOfMaterialRepository;
    @Mock
    private ProductionOrderMapper productionOrderMapper;
    @Mock
    private Pageable pageable;

    @InjectMocks
    private ProductionOrderService productionOrderService;

    private ProductionOrderRequestDTO requestDTO;
    private ProductionOrder order;
    private Product product;
    private ProductionOrderResponseDTO responseDTO;

    private final Long ORDER_ID = 1L;
    private final Long PRODUCT_ID = 10L;
    private final int QUANTITY = 50;
    private final int PRODUCTION_TIME = 2;
    private final LocalDate START_DATE = LocalDate.now().plusDays(1);
    private final LocalDate END_DATE = LocalDate.now().plusDays(5);

    @BeforeEach
    void setUp() {

        product = new Product();
        product.setId(PRODUCT_ID);
        product.setProductionTime(PRODUCTION_TIME);
        product.setStock(100);

        requestDTO = new ProductionOrderRequestDTO();
        requestDTO.setProductId(PRODUCT_ID);
        requestDTO.setQuantity(QUANTITY);
        requestDTO.setStartDate(START_DATE);
        requestDTO.setEndDate(END_DATE);

        order = new ProductionOrder();
        order.setId(ORDER_ID);
        order.setProduct(product);
        order.setQuantity(QUANTITY);
        order.setStartDate(START_DATE);
        order.setEndDate(END_DATE);
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);

        responseDTO = new ProductionOrderResponseDTO();
        responseDTO.setId(ORDER_ID);
        responseDTO.setProductId(PRODUCT_ID);
        responseDTO.setQuantity(QUANTITY);
        responseDTO.setStatus(ProductionOrderStatus.EN_ATTENTE);
    }

    @Test
    void addProductionOrder_Success() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(requestDTO)).thenReturn(order);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(order);
        when(productionOrderMapper.toResponseDto(order)).thenReturn(responseDTO);

        ProductionOrderResponseDTO result = productionOrderService.addProductionOrder(requestDTO);

        assertNotNull(result);
        assertEquals(ProductionOrderStatus.EN_ATTENTE, order.getStatus());
        verify(productionOrderRepository).save(order);
    }

    @Test
    void addProductionOrder_ThrowsProductNotFoundException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productionOrderService.addProductionOrder(requestDTO));
        verify(productionOrderRepository, never()).save(any());
    }

    @Test
    void addProductionOrder_ThrowsBusinessException_InvalidDates() {
        requestDTO.setStartDate(END_DATE.plusDays(1)); // Start Date > End Date
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        assertThrows(BusinessException.class, () -> productionOrderService.addProductionOrder(requestDTO));
        verify(productionOrderRepository, never()).save(any());
    }

    // --- Tests pour getAllProductionOrders ---
    @Test
    void getAllProductionOrders_Success() {
        Page<ProductionOrder> orderPage = new PageImpl<>(Collections.singletonList(order));
        when(productionOrderRepository.findAll(pageable)).thenReturn(orderPage);
        when(productionOrderMapper.toResponseDto(order)).thenReturn(responseDTO);

        Page<ProductionOrderResponseDTO> result = productionOrderService.getAllProductionOrders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productionOrderRepository).findAll(pageable);
    }


    @Test
    void getProductionOrderById_Success() {
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productionOrderMapper.toResponseDto(order)).thenReturn(responseDTO);

        ProductionOrderResponseDTO result = productionOrderService.getProductionOrderById(ORDER_ID);

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());
    }

    @Test
    void getProductionOrderById_ThrowsProductionOrderNotFoundException() {
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ProductionOrderNotFoundException.class, () -> productionOrderService.getProductionOrderById(ORDER_ID));
    }


    @Test
    void updateProductionOrder_Success() {
        ProductionOrderRequestDTO updateDto = new ProductionOrderRequestDTO();
        updateDto.setProductId(PRODUCT_ID);
        updateDto.setQuantity(100);
        updateDto.setStartDate(START_DATE.plusDays(1));
        updateDto.setEndDate(END_DATE.plusDays(1));

        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        doNothing().when(productionOrderMapper).updateEntityFromDto(updateDto, order);

        when(productionOrderRepository.save(order)).thenReturn(order);
        when(productionOrderMapper.toResponseDto(order)).thenReturn(responseDTO);

        ProductionOrderResponseDTO result = productionOrderService.updateProductionOrder(ORDER_ID, updateDto);

        assertNotNull(result);
        verify(productionOrderMapper).updateEntityFromDto(updateDto, order);
        verify(productionOrderRepository).save(order);
    }

    @Test
    void updateProductionOrder_ThrowsProductionOrderNotFoundException() {
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ProductionOrderNotFoundException.class, () -> productionOrderService.updateProductionOrder(ORDER_ID, requestDTO));
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void updateProductionOrder_ThrowsBusinessException_InvalidDates() {
        requestDTO.setStartDate(END_DATE.plusDays(1)); // Start Date > End Date
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        assertThrows(BusinessException.class, () -> productionOrderService.updateProductionOrder(ORDER_ID, requestDTO));
        verify(productionOrderRepository, never()).save(any());
    }


    @Test
    void deleteProductionOrder_Success() {
        when(productionOrderRepository.existsById(ORDER_ID)).thenReturn(true);

        productionOrderService.deleteProductionOrder(ORDER_ID);

        verify(productionOrderRepository).existsById(ORDER_ID);
        verify(productionOrderRepository).deleteById(ORDER_ID);
    }

    @Test
    void deleteProductionOrder_ThrowsProductionOrderNotFoundException() {
        when(productionOrderRepository.existsById(ORDER_ID)).thenReturn(false);

        assertThrows(ProductionOrderNotFoundException.class, () -> productionOrderService.deleteProductionOrder(ORDER_ID));
        verify(productionOrderRepository, never()).deleteById(anyLong());
    }

    @Test
    void getEstimatedProductionTime_Success() {
        // Quantity = 50, ProductionTime = 2. Expected = 100L
        Long expectedTime = (long) QUANTITY * PRODUCTION_TIME;
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        Long result = productionOrderService.getEstimatedProductionTime(ORDER_ID);

        assertEquals(expectedTime, result);
    }

    @Test
    void getEstimatedProductionTime_ThrowsProductionOrderNotFoundException() {
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ProductionOrderNotFoundException.class, () -> productionOrderService.getEstimatedProductionTime(ORDER_ID));
    }

    @Test
    void getEstimatedProductionTime_ThrowsProductNotFoundException() {
        order.setProduct(null);
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThrows(ProductNotFoundException.class, () -> productionOrderService.getEstimatedProductionTime(ORDER_ID));
    }


    @Test
    void blockProductionOrder_Success() {
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);
        ProductionOrder blockedOrder = new ProductionOrder();
        blockedOrder.setStatus(ProductionOrderStatus.BLOQUE);

        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(blockedOrder);

        ProductionOrderResponseDTO blockedResponse = new ProductionOrderResponseDTO();
        blockedResponse.setStatus(ProductionOrderStatus.BLOQUE);
        when(productionOrderMapper.toResponseDto(blockedOrder)).thenReturn(blockedResponse);

        ProductionOrderResponseDTO result = productionOrderService.blockProductionOrder(ORDER_ID);

        assertEquals(ProductionOrderStatus.BLOQUE, result.getStatus());
        verify(productionOrderRepository).save(order);
    }

    @Test
    void blockProductionOrder_ThrowsBusinessException_StatusNotEN_ATTENTE() {
        order.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> productionOrderService.blockProductionOrder(ORDER_ID));
        verify(productionOrderRepository, never()).save(any());
    }

    @Test
    void updateStatus_Success_ToEN_PRODUCTION() {
        String newStatusString = "EN_PRODUCTION";
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);

        ProductionOrder runningOrder = new ProductionOrder();
        runningOrder.setStatus(ProductionOrderStatus.EN_PRODUCTION);

        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(runningOrder);

        ProductionOrderResponseDTO runningResponse = new ProductionOrderResponseDTO();
        runningResponse.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        when(productionOrderMapper.toResponseDto(runningOrder)).thenReturn(runningResponse);

        ProductionOrderResponseDTO result = productionOrderService.updateStatus(ORDER_ID, newStatusString);

        assertEquals(ProductionOrderStatus.EN_PRODUCTION, result.getStatus());
        verify(productRepository, never()).save(any());
        verify(productionOrderRepository).save(order);
    }

    @Test
    void updateStatus_Success_ToTERMINE_UpdatesStock() {
        String newStatusString = "TERMINE";
        order.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        int initialStock = product.getStock(); // 100

        ProductionOrder completedOrder = new ProductionOrder();
        completedOrder.setStatus(ProductionOrderStatus.TERMINE);

        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(completedOrder);

        ProductionOrderResponseDTO completedResponse = new ProductionOrderResponseDTO();
        completedResponse.setStatus(ProductionOrderStatus.TERMINE);
        when(productionOrderMapper.toResponseDto(completedOrder)).thenReturn(completedResponse);

        productionOrderService.updateStatus(ORDER_ID, newStatusString);

        assertEquals(initialStock + QUANTITY, product.getStock());
        verify(productRepository).save(product);
        verify(productionOrderRepository).save(order);
    }

    @Test
    void updateStatus_ThrowsInvalidOrderStatusException() {
        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () -> productionOrderService.updateStatus(ORDER_ID, "STATUT_INVALIDE"));
    }

    @Test
    void updateStatus_ThrowsBusinessException_AlreadyTERMINE() {
        order.setStatus(ProductionOrderStatus.TERMINE);
        String newStatusString = "TERMINE";

        when(productionOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> productionOrderService.updateStatus(ORDER_ID, newStatusString));
        verify(productionOrderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }
}