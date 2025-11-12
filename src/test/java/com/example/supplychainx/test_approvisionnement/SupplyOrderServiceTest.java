package com.example.supplychainx.test_approvisionnement;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem.SupplyOrderItemDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.*;
import com.example.supplychainx.service_approvisionnement.mapper.SupplyOrderItemMapper;
import com.example.supplychainx.service_approvisionnement.mapper.SupplyOrderMapper;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_approvisionnement.model.Supplier;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrder;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrderItem;
import com.example.supplychainx.service_approvisionnement.model.enums.SupplyOrderStatus;
import com.example.supplychainx.service_approvisionnement.repository.RawMaterialRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplierRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplyOrderRepository;
import com.example.supplychainx.service_approvisionnement.service.SupplyOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyOrderServiceTest {

    @Mock
    private SupplyOrderRepository orderRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private RawMaterialRepository rawMaterialRepository;
    @Mock
    private SupplyOrderMapper orderMapper;
    @Mock
    private SupplyOrderItemMapper itemMapper;

    @InjectMocks
    private SupplyOrderService supplyOrderService;

    private final Long ORDER_ID = 1L;
    private final Long SUPPLIER_ID = 5L;
    private final Long MATERIAL_ID = 10L;
    private final int QUANTITY = 100;

    private SupplyOrderRequestDTO orderRequestDTO;
    private SupplyOrder supplyOrder;
    private Supplier supplier;
    private RawMaterial rawMaterial;
    private SupplyOrderItem supplyOrderItem;

    @BeforeEach
    void setUp() {
        // Objets de base
        supplier = new Supplier();
        supplier.setId(SUPPLIER_ID);

        rawMaterial = new RawMaterial();
        rawMaterial.setId(MATERIAL_ID);
        rawMaterial.setStock(200); // Stock initial

        supplyOrder = new SupplyOrder();
        supplyOrder.setId(ORDER_ID);
        supplyOrder.setSupplier(supplier);
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);

        supplyOrderItem = new SupplyOrderItem();
        supplyOrderItem.setSupplyOrder(supplyOrder);
        supplyOrderItem.setRawMaterial(rawMaterial);
        supplyOrderItem.setQuantity(QUANTITY); // Quantité à recevoir (100)

        supplyOrder.setOrderItems(Collections.singletonList(supplyOrderItem));

        // DTOs
        SupplyOrderItemDTO itemDTO = new SupplyOrderItemDTO();
        itemDTO.setMaterialId(MATERIAL_ID);
        itemDTO.setQuantity(QUANTITY);

        orderRequestDTO = new SupplyOrderRequestDTO();
        orderRequestDTO.setSupplierId(SUPPLIER_ID);
        orderRequestDTO.setOrderItems(Collections.singletonList(itemDTO));
    }

    // --- Tests pour createOrder ---
    @Test
    void createOrder_Success() {
        // Arrange
        when(orderMapper.toEntity(orderRequestDTO)).thenReturn(supplyOrder);
        when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
        when(rawMaterialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(rawMaterial));

        // Simuler le mappage de l'item
        when(itemMapper.toEntity(any(SupplyOrderItemDTO.class))).thenReturn(supplyOrderItem);

        when(orderRepository.save(any(SupplyOrder.class))).thenReturn(supplyOrder);

        // Act
        SupplyOrder createdOrder = supplyOrderService.createOrder(orderRequestDTO);

        // Assert
        assertNotNull(createdOrder);
        assertEquals(SUPPLIER_ID, createdOrder.getSupplier().getId());
        assertEquals(SupplyOrderStatus.EN_ATTENTE, createdOrder.getStatus());
        assertNotNull(createdOrder.getOrderdate());
        verify(orderRepository).save(supplyOrder);
    }

    @Test
    void createOrder_ThrowsSupplierNotFoundException() {
        // Arrange
        when(orderMapper.toEntity(orderRequestDTO)).thenReturn(supplyOrder);
        when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SupplierNotFoundException.class, () -> supplyOrderService.createOrder(orderRequestDTO));
        verify(rawMaterialRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ThrowsRawMaterialNotFoundException() {
        // Arrange
        when(orderMapper.toEntity(orderRequestDTO)).thenReturn(supplyOrder);
        when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));

        // Simuler le mappage de l'item
        when(itemMapper.toEntity(any(SupplyOrderItemDTO.class))).thenReturn(supplyOrderItem);

        when(rawMaterialRepository.findById(MATERIAL_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RawMaterialNotFoundException.class, () -> supplyOrderService.createOrder(orderRequestDTO));
        verify(orderRepository, never()).save(any());
    }

    // --- Tests pour getOrderById ---
    @Test
    void getOrderById_Success() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(supplyOrder));

        // Act
        SupplyOrder result = supplyOrderService.getOrderById(ORDER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());
    }

    @Test
    void getOrderById_ThrowsSupplyOrderNotFoundException() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SupplyOrderNotFoundException.class, () -> supplyOrderService.getOrderById(ORDER_ID));
    }

    // --- Tests pour getAllOrders ---
    @Test
    void getAllOrders_Success() {
        // Arrange
        List<SupplyOrder> orders = Collections.singletonList(supplyOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<SupplyOrder> result = supplyOrderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    // --- Tests pour updateOrderStatus ---

    @Test
    void updateOrderStatus_Success_StatusUpdateWithoutStockChange() {
        // Arrange
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);
        String newStatusString = "EN_COURS"; // Statut qui ne modifie pas le stock

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(supplyOrder));
        when(orderRepository.save(any(SupplyOrder.class))).thenReturn(supplyOrder);

        // Act
        SupplyOrder updatedOrder = supplyOrderService.updateOrderStatus(ORDER_ID, newStatusString);

        // Assert
        assertEquals(SupplyOrderStatus.EN_COURS, updatedOrder.getStatus());
        verify(rawMaterialRepository, never()).save(any());
        verify(orderRepository).save(supplyOrder);
    }

    @Test
    void updateOrderStatus_Success_ToRECUE_UpdatesStock() {
        // Arrange
        int initialStock = rawMaterial.getStock(); // 200
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);
        String newStatusString = "RECUE";

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(supplyOrder));

        // Simuler la sauvegarde de la Matière Première
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(rawMaterial);

        // Simuler la sauvegarde de la Commande
        when(orderRepository.save(any(SupplyOrder.class))).thenReturn(supplyOrder);

        // Act
        SupplyOrder updatedOrder = supplyOrderService.updateOrderStatus(ORDER_ID, newStatusString);

        // Assert
        assertEquals(SupplyOrderStatus.RECUE, updatedOrder.getStatus());
        // Vérification du stock: initial (200) + reçu (100) = 300
        assertEquals(initialStock + QUANTITY, rawMaterial.getStock());
        verify(rawMaterialRepository).save(rawMaterial);
        verify(orderRepository).save(supplyOrder);
    }

    @Test
    void updateOrderStatus_ThrowsSupplyOrderNotFoundException() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SupplyOrderNotFoundException.class,
                () -> supplyOrderService.updateOrderStatus(ORDER_ID, "RECUE"));
    }

    @Test
    void updateOrderStatus_ThrowsInvalidOrderStatusException() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(supplyOrder));

        // Act & Assert
        assertThrows(InvalidOrderStatusException.class,
                () -> supplyOrderService.updateOrderStatus(ORDER_ID, "STATUT_INCONNU"));
    }

    @Test
    void updateOrderStatus_ThrowsOrderIsReceivedException_ChangeFromRECUE() {
        // Arrange
        supplyOrder.setStatus(SupplyOrderStatus.RECUE); // Déjà reçu
        String newStatusString = "EN_ATTENTE"; // Tentative de retour

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(supplyOrder));

        // Act & Assert
        assertThrows(OrderIsReceivedException.class,
                () -> supplyOrderService.updateOrderStatus(ORDER_ID, newStatusString));
        verify(orderRepository, never()).save(any());
    }

    // --- Tests pour deleteOrder ---

    @Test
    void deleteOrder_Success() {
        // Arrange
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE); // Statut non RECUE
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(supplyOrder));

        // Act
        supplyOrderService.deleteOrder(ORDER_ID);

        // Assert
        verify(orderRepository).delete(supplyOrder);
    }

    @Test
    void deleteOrder_ThrowsSupplyOrderNotFoundException() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SupplyOrderNotFoundException.class, () -> supplyOrderService.deleteOrder(ORDER_ID));
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void deleteOrder_ThrowsOrderIsReceivedException() {
        // Arrange
        supplyOrder.setStatus(SupplyOrderStatus.RECUE); // Statut interdit à la suppression
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(supplyOrder));

        // Act & Assert
        assertThrows(OrderIsReceivedException.class, () -> supplyOrderService.deleteOrder(ORDER_ID));
        verify(orderRepository, never()).delete(any());
    }
}
