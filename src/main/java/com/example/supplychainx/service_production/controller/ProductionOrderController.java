package com.example.supplychainx.service_production.controller;

import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderRequestDTO;
import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderResponseDTO;
import com.example.supplychainx.service_production.service.ProductionOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
public class ProductionOrderController {
    private final ProductionOrderService productionOrderService;

    @PostMapping
    public ResponseEntity<ProductionOrderResponseDTO> addProductionOrder(@Valid @RequestBody ProductionOrderRequestDTO dto) {
        ProductionOrderResponseDTO response = productionOrderService.addProductionOrder(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ProductionOrderResponseDTO>> getAllProductionOrders(Pageable pageable) {
        Page<ProductionOrderResponseDTO> orderPage = productionOrderService.getAllProductionOrders(pageable);
        return ResponseEntity.ok(orderPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderResponseDTO> getProductionOrderById(@PathVariable Long id) {
        ProductionOrderResponseDTO response = productionOrderService.getProductionOrderById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionOrderResponseDTO> updateProductionOrder(@PathVariable Long id, @Valid @RequestBody ProductionOrderRequestDTO dto) {
        ProductionOrderResponseDTO response = productionOrderService.updateProductionOrder(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductionOrder(@PathVariable Long id) {
        productionOrderService.deleteProductionOrder(id);
        return ResponseEntity.noContent().build();
    }
}
