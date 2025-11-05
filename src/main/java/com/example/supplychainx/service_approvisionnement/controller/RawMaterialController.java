package com.example.supplychainx.service_approvisionnement.controller;

import com.example.supplychainx.annotations.RoleRequired;
import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialResponseDTO;
import com.example.supplychainx.service_approvisionnement.service.RawMaterialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@RoleRequired({"GESTIONNAIRE_APPROVISIONNEMENT","ADMIN"})
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    @PostMapping
    public ResponseEntity<RawMaterialResponseDTO> create(@RequestBody RawMaterialRequestDTO dto) {
        return ResponseEntity.ok(rawMaterialService.createMaterial(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rawMaterialService.getMaterialById(id));
    }

    @RoleRequired({"GESTIONNAIRE_APPROVISIONNEMENT","SUPERVISEUR_LOGISTIQUE", "ADMIN"})
    @GetMapping
    public ResponseEntity<List<RawMaterialResponseDTO>> getAll() {
        return ResponseEntity.ok(rawMaterialService.getAllMaterials());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialResponseDTO> update(@PathVariable Long id, @RequestBody RawMaterialRequestDTO dto) {
        return ResponseEntity.ok(rawMaterialService.updateMaterial(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rawMaterialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<RawMaterialResponseDTO>> getLowStockRawMaterials() {
        return ResponseEntity.ok(rawMaterialService.getLowStockRawMaterials());
    }
}