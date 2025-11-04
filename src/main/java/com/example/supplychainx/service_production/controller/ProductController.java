package com.example.supplychainx.service_production.controller;

import com.example.supplychainx.annotations.RoleRequired;
import com.example.supplychainx.service_production.dto.product.ProductRequestDTO;
import com.example.supplychainx.service_production.dto.product.ProductResponseDTO;
import com.example.supplychainx.service_production.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.addProduct(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(Pageable pageable) {
        Page<ProductResponseDTO> productPage = productService.getAllProducts(pageable);
        return ResponseEntity.ok(productPage);
    }

    @RoleRequired({"GESTIONNAIRE_APPROVISIONNEMENT"})
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.updateProduct(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByName(@RequestParam("name") String name){
        List<ProductResponseDTO> products = productService.getProductByName(name);
        return ResponseEntity.ok(products);
    }
}
