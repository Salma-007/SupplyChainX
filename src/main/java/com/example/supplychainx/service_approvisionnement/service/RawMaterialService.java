package com.example.supplychainx.service_approvisionnement.service;

import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialResponseDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.RawMaterialNotFoundException;
import com.example.supplychainx.service_approvisionnement.mapper.RawMaterialMapper;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_approvisionnement.repository.RawMaterialRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;
    private final SupplierRepository supplierRepository;

    public RawMaterialResponseDTO createMaterial(RawMaterialRequestDTO dto){
        RawMaterial material = rawMaterialMapper.toEntity(dto);
        if (dto.getSupplierIds() != null) {
            material.setSuppliers(
                    new HashSet<>(supplierRepository.findAllById(dto.getSupplierIds()))
            );
        }
        RawMaterial saved = rawMaterialRepository.save(material);
        return rawMaterialMapper.toResponseDto(saved);
    }

    public RawMaterialResponseDTO getMaterialById(Long id){
        RawMaterial material = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RawMaterialNotFoundException("Raw material not found with id: " + id));
        return rawMaterialMapper.toResponseDto(material);
    }

    public List<RawMaterialResponseDTO> getAllMaterials(){
        List<RawMaterial> materials = rawMaterialRepository.findAll();
        return materials.stream()
                .map(rawMaterialMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteMaterial(Long id){
        if(!rawMaterialRepository.existsById(id)){
            throw new RawMaterialNotFoundException("Raw material not found with id: " + id);
        }
        rawMaterialRepository.deleteById(id);
    }

    public RawMaterialResponseDTO updateMaterial(Long id, RawMaterialRequestDTO dto){
        RawMaterial existing = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RawMaterialNotFoundException("Raw material not found with id: " + id));

        existing.setName(dto.getName());
        existing.setStock(dto.getStock());
        existing.setUnit(dto.getUnit());
        existing.setStockMin(dto.getStockMin());

        RawMaterial updated = rawMaterialRepository.save(existing);
        return rawMaterialMapper.toResponseDto(updated);
    }
}
