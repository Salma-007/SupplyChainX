package com.example.supplychainx.service_production.service;

import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_production.dto.product.ProductRequestDTO;
import com.example.supplychainx.service_production.dto.product.ProductResponseDTO;
import com.example.supplychainx.service_production.exceptions.ProductNotFoundException;
import com.example.supplychainx.service_production.mapper.ProductMapper;
import com.example.supplychainx.service_production.model.Product;
import com.example.supplychainx.service_production.repository.ProductRepository;
import com.example.supplychainx.service_production.repository.ProductionOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductionOrderRepository productionOrderRepository;

    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO dto){
        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return productMapper.toResponseDto(saved);
    }

    @Transactional
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable){
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toResponseDto);
    }

    @Transactional
    public void deleteProduct(Long id){
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException("Produit non trouvé avec id: " + id);
        }
        if (productionOrderRepository.existsByProductId(id)) {
            throw new BusinessException("Impossible de supprimer le produit. Des ordres de production y sont associés.");
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto){
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec id: " + id));

        existing.setName(dto.getName());
        existing.setProductionTime(dto.getProductionTime());
        existing.setCost(dto.getCost());
        existing.setStock(dto.getStock());

        Product updated = productRepository.save(existing);
        return productMapper.toResponseDto(updated);
    }

    public ProductResponseDTO getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec id: " + id));
        return productMapper.toResponseDto(product);
    }

    public List<ProductResponseDTO> getProductByName(String name){
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream().map(productMapper::toResponseDto).collect(Collectors.toList());
    }
}
