package com.example.supplychainx.test_approvisionnement;

import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialResponseDTO;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierSimpleDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_approvisionnement.exceptions.RawMaterialNotFoundException;
import com.example.supplychainx.service_approvisionnement.mapper.RawMaterialMapper;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_approvisionnement.repository.RawMaterialRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplierRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplyOrderItemRepository;
import com.example.supplychainx.service_approvisionnement.service.RawMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RawMaterialServiceTest {
    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private RawMaterialMapper rawMaterialMapper;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplyOrderItemRepository supplyOrderItemRepository;

    @InjectMocks
    private RawMaterialService rawMaterialService;

    private RawMaterialRequestDTO requestDTO;
    private RawMaterialResponseDTO responseDTO;
    private RawMaterial rawMaterial;
    private final Long MATERIAL_ID = 1L;

    @BeforeEach
    void setUp() {
        List<SupplierSimpleDTO> list = null;
        List<Long> listLong = null;
        requestDTO = new RawMaterialRequestDTO(
                "Acier Inoxydable",
                100,
                10,
                "kg",
                listLong
        );

        responseDTO = new RawMaterialResponseDTO(
                MATERIAL_ID,
                "Acier Inoxydable",
                100,
                10,
                "kg",
                list
        );

        rawMaterial = new RawMaterial();
        rawMaterial.setId(MATERIAL_ID);
        rawMaterial.setName("Acier Inoxydable");
        rawMaterial.setStock(100);
        rawMaterial.setUnit("kg");
        rawMaterial.setStockMin(20);
    }


    @Test
    void createMaterial_shouldReturnResponseDTO() {

        when(rawMaterialMapper.toEntity(requestDTO)).thenReturn(rawMaterial);
        when(rawMaterialRepository.save(rawMaterial)).thenReturn(rawMaterial);
        when(rawMaterialMapper.toResponseDto(rawMaterial)).thenReturn(responseDTO);

        RawMaterialResponseDTO result = rawMaterialService.createMaterial(requestDTO);

        assertNotNull(result);
        assertEquals(MATERIAL_ID, result.getId());
        assertEquals("Acier Inoxydable", result.getName());
        verify(rawMaterialRepository, times(1)).save(rawMaterial);
    }

    @Test
    void getMaterialById_shouldReturnResponseDTO_whenFound() {
        // GIVEN
        when(rawMaterialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(rawMaterial));
        when(rawMaterialMapper.toResponseDto(rawMaterial)).thenReturn(responseDTO);

        // WHEN
        RawMaterialResponseDTO result = rawMaterialService.getMaterialById(MATERIAL_ID);

        // THEN
        assertNotNull(result);
        assertEquals(MATERIAL_ID, result.getId());
    }

    @Test
    void getMaterialById_shouldThrowException_whenNotFound() {

        when(rawMaterialRepository.findById(MATERIAL_ID)).thenReturn(Optional.empty());

        assertThrows(RawMaterialNotFoundException.class, () -> rawMaterialService.getMaterialById(MATERIAL_ID));
    }


    @Test
    void getAllMaterials_shouldReturnListOfResponseDTOs() {
        List<RawMaterial> materials = Arrays.asList(rawMaterial, new RawMaterial());
        List<RawMaterialResponseDTO> expectedResponses = Arrays.asList(responseDTO, new RawMaterialResponseDTO());

        when(rawMaterialRepository.findAll()).thenReturn(materials);
        when(rawMaterialMapper.toResponseDto(any(RawMaterial.class)))
                .thenReturn(responseDTO)
                .thenReturn(new RawMaterialResponseDTO());

        List<RawMaterialResponseDTO> result = rawMaterialService.getAllMaterials();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(rawMaterialRepository, times(1)).findAll();
    }


    @Test
    void updateMaterial_shouldThrowException_whenNotFound() {
        // GIVEN
        when(rawMaterialRepository.findById(MATERIAL_ID)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(RawMaterialNotFoundException.class, () -> rawMaterialService.updateMaterial(MATERIAL_ID, requestDTO));
    }

    @Test
    void deleteMaterial_shouldSucceed_whenFoundAndNotInUse() {
        // GIVEN
        when(rawMaterialRepository.existsById(MATERIAL_ID)).thenReturn(true);
        when(supplyOrderItemRepository.existsByRawMaterialId(MATERIAL_ID)).thenReturn(false);
        doNothing().when(rawMaterialRepository).deleteById(MATERIAL_ID);

        // WHEN
        rawMaterialService.deleteMaterial(MATERIAL_ID);

        // THEN
        verify(rawMaterialRepository, times(1)).deleteById(MATERIAL_ID);
    }

    @Test
    void deleteMaterial_shouldThrowNotFoundException_whenNotFound() {
        // GIVEN
        when(rawMaterialRepository.existsById(MATERIAL_ID)).thenReturn(false);

        // WHEN / THEN
        assertThrows(RawMaterialNotFoundException.class, () -> rawMaterialService.deleteMaterial(MATERIAL_ID));
        verify(rawMaterialRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteMaterial_shouldThrowBusinessException_whenUsedInSupplyOrder() {
        // GIVEN
        when(rawMaterialRepository.existsById(MATERIAL_ID)).thenReturn(true);
        when(supplyOrderItemRepository.existsByRawMaterialId(MATERIAL_ID)).thenReturn(true);

        // WHEN / THEN
        assertThrows(BusinessException.class, () -> rawMaterialService.deleteMaterial(MATERIAL_ID));
        verify(rawMaterialRepository, never()).deleteById(anyLong());
    }

    @Test
    void getLowStockRawMaterials_shouldReturnLowStockMaterials() {
        // GIVEN
        RawMaterial lowStockMaterial = new RawMaterial();
        lowStockMaterial.setId(2L);
        lowStockMaterial.setName("Plastique PET");
        lowStockMaterial.setStock(10);
        lowStockMaterial.setStockMin(15);

        RawMaterialResponseDTO lowStockResponseDTO = new RawMaterialResponseDTO();
        lowStockResponseDTO.setId(2L);
        lowStockResponseDTO.setName("Plastique PET");

        List<RawMaterial> lowStockList = Collections.singletonList(lowStockMaterial);

        when(rawMaterialRepository.findLowStockRawMaterials()).thenReturn(lowStockList);
        when(rawMaterialMapper.toResponseDto(lowStockMaterial)).thenReturn(lowStockResponseDTO);

        // WHEN
        List<RawMaterialResponseDTO> result = rawMaterialService.getLowStockRawMaterials();

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Plastique PET", result.get(0).getName());
        verify(rawMaterialRepository, times(1)).findLowStockRawMaterials();
    }
}
