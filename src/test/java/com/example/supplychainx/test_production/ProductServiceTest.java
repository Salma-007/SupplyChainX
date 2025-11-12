package com.example.supplychainx.test_production;
import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_approvisionnement.repository.RawMaterialRepository;
import com.example.supplychainx.service_production.dto.billOfMaterial.BillOfMaterialRequestDTO;
import com.example.supplychainx.service_production.dto.billOfMaterial.BillOfMaterialResponseDTO;
import com.example.supplychainx.service_production.dto.product.ProductRequestDTO;
import com.example.supplychainx.service_production.dto.product.ProductResponseDTO;
import com.example.supplychainx.service_production.exceptions.ProductNotFoundException;
import com.example.supplychainx.service_production.mapper.BillOfMaterialMapper;
import com.example.supplychainx.service_production.mapper.ProductMapper;
import com.example.supplychainx.service_production.model.BillOfMaterial;
import com.example.supplychainx.service_production.model.Product;
import com.example.supplychainx.service_production.repository.ProductRepository;
import com.example.supplychainx.service_production.repository.ProductionOrderRepository;
import com.example.supplychainx.service_production.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private BillOfMaterialMapper billMapper;

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;
    private Product product;
    private RawMaterial rawMaterial;
    private BillOfMaterial billOfMaterial;
    private final Long PRODUCT_ID = 1L;
    private final Long RAW_MATERIAL_ID = 10L;

    @BeforeEach
    void setUp() {
        rawMaterial = new RawMaterial();
        rawMaterial.setId(RAW_MATERIAL_ID);
        rawMaterial.setName("Acier");

        BillOfMaterialRequestDTO bomRequestDTO = new BillOfMaterialRequestDTO(RAW_MATERIAL_ID, 5);
        BillOfMaterialResponseDTO bomResponseDTO = new BillOfMaterialResponseDTO(1L, "Acier", 5);

        billOfMaterial = new BillOfMaterial();
        billOfMaterial.setQuantity(5);
        billOfMaterial.setRawMaterial(rawMaterial);

        requestDTO = new ProductRequestDTO(
                "Table A",
                60,
                150.0,
                10,
                Collections.singletonList(bomRequestDTO)
        );

        responseDTO = new ProductResponseDTO();
        responseDTO.setId(PRODUCT_ID);
        responseDTO.setName("Table A");
        responseDTO.setProductionTime(60);
        responseDTO.setCost(150.0);
        responseDTO.setStock(10);
        responseDTO.setBills(Collections.singletonList(bomResponseDTO));

        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Table A");
        product.setProductionTime(60);
        product.setCost(150.0);
        product.setStock(10);
        product.setBills(Collections.singletonList(billOfMaterial));
        billOfMaterial.setProduct(product);
    }


    @Test
    void addProduct_shouldCreateProductWithBOM() {
        when(productMapper.toEntity(requestDTO)).thenReturn(product);
        when(rawMaterialRepository.findById(RAW_MATERIAL_ID)).thenReturn(Optional.of(rawMaterial));
        when(billMapper.toEntity(any(BillOfMaterialRequestDTO.class))).thenReturn(billOfMaterial);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.addProduct(requestDTO);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
        assertEquals(1, result.getBills().size());
        verify(productRepository, times(1)).save(product);
        assertEquals(product, product.getBills().get(0).getProduct());
        assertEquals(rawMaterial, product.getBills().get(0).getRawMaterial());
    }

    @Test
    void getProductById_shouldReturnResponseDTO_whenFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.getProductById(PRODUCT_ID);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
    }

    @Test
    void getProductById_shouldThrowException_whenNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(PRODUCT_ID));
    }

    @Test
    void getProductByName_shouldReturnListOfResponseDTOs() {

        List<Product> products = Collections.singletonList(product);
        List<ProductResponseDTO> expectedResponses = Collections.singletonList(responseDTO);

        when(productRepository.findByNameContainingIgnoreCase("Table")).thenReturn(products);
        when(productMapper.toResponseDto(product)).thenReturn(responseDTO);

        List<ProductResponseDTO> result = productService.getProductByName("Table");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Table A", result.get(0).getName());
    }

    @Test
    void getAllProducts_shouldReturnPagedResponseDTOs() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product), pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toResponseDto(product)).thenReturn(responseDTO);

        Page<ProductResponseDTO> resultPage = productService.getAllProducts(pageable);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("Table A", resultPage.getContent().get(0).getName());
    }

    // --- Update Tests ---

    @Test
    void updateProduct_shouldReturnUpdatedResponseDTO_whenFound() {
        ProductRequestDTO updateDTO = new ProductRequestDTO(
                "Chaise B",
                30,
                75.0,
                25,
                requestDTO.getBills()
        );

        Product updatedProduct = new Product();
        updatedProduct.setId(PRODUCT_ID);
        updatedProduct.setName("Chaise B");
        updatedProduct.setProductionTime(30);
        updatedProduct.setCost(75.0);
        updatedProduct.setStock(25);

        ProductResponseDTO updatedResponseDTO = new ProductResponseDTO();
        updatedResponseDTO.setId(PRODUCT_ID);
        updatedResponseDTO.setName("Chaise B");
        updatedResponseDTO.setProductionTime(30);
        updatedResponseDTO.setCost(75.0);
        updatedResponseDTO.setStock(25);


        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(productMapper.toResponseDto(updatedProduct)).thenReturn(updatedResponseDTO);

        ProductResponseDTO result = productService.updateProduct(PRODUCT_ID, updateDTO);

        assertNotNull(result);
        assertEquals("Chaise B", result.getName());
        assertEquals(75.0, result.getCost());
        assertEquals("Chaise B", product.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_shouldThrowException_whenNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(PRODUCT_ID, requestDTO));
    }

    @Test
    void deleteProduct_shouldSucceed_whenFoundAndNoProductionOrder() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(true);
        when(productionOrderRepository.existsByProductId(PRODUCT_ID)).thenReturn(false);
        doNothing().when(productRepository).deleteById(PRODUCT_ID);

        productService.deleteProduct(PRODUCT_ID);

        verify(productRepository, times(1)).deleteById(PRODUCT_ID);
    }

    @Test
    void deleteProduct_shouldThrowNotFoundException_whenNotFound() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(PRODUCT_ID));
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteProduct_shouldThrowBusinessException_whenUsedInProductionOrder() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(true);
        when(productionOrderRepository.existsByProductId(PRODUCT_ID)).thenReturn(true);

        assertThrows(BusinessException.class, () -> productService.deleteProduct(PRODUCT_ID));
        verify(productRepository, never()).deleteById(anyLong());
    }
}
