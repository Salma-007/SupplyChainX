package com.example.supplychainx.service_approvisionnement.controller;


import com.example.supplychainx.annotations.RoleRequired;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierResponseDTO;
import com.example.supplychainx.service_approvisionnement.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@RoleRequired({"GESTIONNAIRE_APPROVISIONNEMENT"})
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(@Valid @RequestBody SupplierRequestDTO dto) {
        return ResponseEntity.ok(supplierService.createSupplier(dto));
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAll() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> update(@PathVariable Long id, @Valid @RequestBody SupplierRequestDTO dto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<SupplierResponseDTO> search(@RequestParam String name) {
        return ResponseEntity.ok(supplierService.getSupplierByName(name));
    }

}
