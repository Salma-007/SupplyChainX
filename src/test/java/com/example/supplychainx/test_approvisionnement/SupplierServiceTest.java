package com.example.supplychainx.test_approvisionnement;

import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierResponseDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_approvisionnement.exceptions.SupplierNotFoundException;
import com.example.supplychainx.service_approvisionnement.mapper.SupplierMapper;
import com.example.supplychainx.service_approvisionnement.model.Supplier;
import com.example.supplychainx.service_approvisionnement.repository.SupplierRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplyOrderRepository;
import com.example.supplychainx.service_approvisionnement.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private SupplierMapper supplierMapper;
    @Mock
    private SupplyOrderRepository supplyOrderRepository;

    @InjectMocks
    private SupplierService supplierService;

    private SupplierRequestDTO supplierRequestDTO;
    private Supplier supplier;
    private SupplierResponseDTO supplierResponseDTO;
    private final Long SUPPLIER_ID = 1L;
    private final String SUPPLIER_NAME = "Fournisseur A";

    @BeforeEach
    void setUp() {
        supplierRequestDTO = new SupplierRequestDTO();
        supplierRequestDTO.setName(SUPPLIER_NAME);
        supplierRequestDTO.setContact("contact@a.com");
        supplierRequestDTO.setRating(4.5);
        supplierRequestDTO.setLeadTime(7);

        supplier = new Supplier();
        supplier.setId(SUPPLIER_ID);
        supplier.setName(SUPPLIER_NAME);
        supplier.setContact("contact@a.com");
        supplier.setRating(4.5);
        supplier.setLeadTime(7);

        supplierResponseDTO = new SupplierResponseDTO();
        supplierResponseDTO.setId(SUPPLIER_ID);
        supplierResponseDTO.setName(SUPPLIER_NAME);
        supplierResponseDTO.setContact("contact@a.com");
        supplierResponseDTO.setRating(4.5);
        supplierResponseDTO.setLeadTime(7);
    }


    @Test
    void createSupplier_Success() {
        when(supplierMapper.toEntity(supplierRequestDTO)).thenReturn(supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toResponseDto(supplier)).thenReturn(supplierResponseDTO);

        SupplierResponseDTO result = supplierService.createSupplier(supplierRequestDTO);

        assertNotNull(result);
        assertEquals(SUPPLIER_ID, result.getId());
        verify(supplierRepository).save(supplier);
    }

    // --- Tests pour getAllSuppliers ---
    @Test
    void getAllSuppliers_Success() {
        List<Supplier> suppliers = Arrays.asList(supplier, new Supplier());
        when(supplierRepository.findAll()).thenReturn(suppliers);
        when(supplierMapper.toResponseDto(any(Supplier.class)))
                .thenReturn(supplierResponseDTO)
                .thenReturn(new SupplierResponseDTO());

        List<SupplierResponseDTO> result = supplierService.getAllSuppliers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(supplierRepository).findAll();
    }


    @Test
    void getSupplierById_Success() {
        when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponseDto(supplier)).thenReturn(supplierResponseDTO);

        SupplierResponseDTO result = supplierService.getSupplierById(SUPPLIER_ID);

        assertNotNull(result);
        assertEquals(SUPPLIER_ID, result.getId());
    }

    @Test
    void getSupplierById_ThrowsSupplierNotFoundException() {
        when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.getSupplierById(SUPPLIER_ID));
    }

    @Test
    void updateSupplier_Success() {
        SupplierRequestDTO updateDto = new SupplierRequestDTO();
        updateDto.setName("New Name");
        updateDto.setRating(5.0);
        updateDto.setContact("new@contact.com");
        updateDto.setLeadTime(14);

        Supplier updatedSupplier = new Supplier();
        updatedSupplier.setId(SUPPLIER_ID);
        updatedSupplier.setName("New Name");

        SupplierResponseDTO expectedResponse = new SupplierResponseDTO();
        expectedResponse.setId(SUPPLIER_ID);
        expectedResponse.setName("New Name");
        expectedResponse.setRating(5.0);

        when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(updatedSupplier);
        when(supplierMapper.toResponseDto(updatedSupplier)).thenReturn(expectedResponse);

        SupplierResponseDTO result = supplierService.updateSupplier(SUPPLIER_ID, updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals(5.0, result.getRating());

        // Vérifier que l'entité a été mise à jour avant la sauvegarde
        assertEquals("New Name", supplier.getName());
        assertEquals(5.0, supplier.getRating());

        verify(supplierRepository).save(supplier);
    }

    @Test
    void updateSupplier_ThrowsSupplierNotFoundException() {
        when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.updateSupplier(SUPPLIER_ID, supplierRequestDTO));
        verify(supplierRepository, never()).save(any(Supplier.class));
    }


    @Test
    void deleteSupplier_Success() {
        when(supplierRepository.existsById(SUPPLIER_ID)).thenReturn(true);
        when(supplyOrderRepository.existsBySupplierId(SUPPLIER_ID)).thenReturn(false);

        supplierService.deleteSupplier(SUPPLIER_ID);

        verify(supplierRepository).existsById(SUPPLIER_ID);
        verify(supplyOrderRepository).existsBySupplierId(SUPPLIER_ID);
        verify(supplierRepository).deleteById(SUPPLIER_ID);
    }

    @Test
    void deleteSupplier_ThrowsSupplierNotFoundException() {
        when(supplierRepository.existsById(SUPPLIER_ID)).thenReturn(false);

        assertThrows(SupplierNotFoundException.class, () -> supplierService.deleteSupplier(SUPPLIER_ID));
        verify(supplierRepository, never()).deleteById(anyLong());
        verify(supplyOrderRepository, never()).existsBySupplierId(anyLong());
    }

    @Test
    void deleteSupplier_ThrowsBusinessException_OrdersExist() {
        when(supplierRepository.existsById(SUPPLIER_ID)).thenReturn(true);
        when(supplyOrderRepository.existsBySupplierId(SUPPLIER_ID)).thenReturn(true);

        assertThrows(BusinessException.class, () -> supplierService.deleteSupplier(SUPPLIER_ID));
        verify(supplierRepository, never()).deleteById(anyLong());
    }


    @Test
    void getSupplierByName_Success() {
        when(supplierRepository.findByNameIgnoreCase(SUPPLIER_NAME)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponseDto(supplier)).thenReturn(supplierResponseDTO);

        SupplierResponseDTO result = supplierService.getSupplierByName(SUPPLIER_NAME);

        assertNotNull(result);
        assertEquals(SUPPLIER_NAME, result.getName());
        verify(supplierRepository).findByNameIgnoreCase(SUPPLIER_NAME);
    }

    @Test
    void getSupplierByName_ThrowsSupplierNotFoundException() {
        String nonExistentName = "Inconnu";
        when(supplierRepository.findByNameIgnoreCase(nonExistentName)).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.getSupplierByName(nonExistentName));
        verify(supplierRepository).findByNameIgnoreCase(nonExistentName);
    }
}