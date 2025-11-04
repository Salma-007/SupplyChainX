package com.example.supplychainx.service_delivery.controller;

import com.example.supplychainx.annotations.RoleRequired;
import com.example.supplychainx.service_delivery.dto.delivery.DeliveryRequestDTO;
import com.example.supplychainx.service_delivery.dto.delivery.DeliveryResponseDTO;
import com.example.supplychainx.service_delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@RoleRequired({"SUPERVISEUR_LIVRAISONS"})
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@Valid @RequestBody DeliveryRequestDTO dto) {
        DeliveryResponseDTO response = deliveryService.createDelivery(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<DeliveryResponseDTO>> getAllDeliveries(Pageable pageable) {
        Page<DeliveryResponseDTO> deliveryPage = deliveryService.getAllDeliveries(pageable);
        return ResponseEntity.ok(deliveryPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryById(@PathVariable Long id) {
        DeliveryResponseDTO response = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> updateDelivery(@PathVariable Long id, @Valid @RequestBody DeliveryRequestDTO dto) {
        DeliveryResponseDTO response = deliveryService.updateDelivery(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}
