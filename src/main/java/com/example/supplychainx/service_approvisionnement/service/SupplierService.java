package com.example.supplychainx.service_approvisionnement.service;

import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierResponseDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.BusinessException;
import com.example.supplychainx.service_approvisionnement.exceptions.SupplierNotFoundException;
import com.example.supplychainx.service_approvisionnement.mapper.SupplierMapper;
import com.example.supplychainx.service_approvisionnement.model.Supplier;
import com.example.supplychainx.service_approvisionnement.repository.SupplierRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplyOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final SupplyOrderRepository supplyOrderRepository;


    public SupplierResponseDTO createSupplier(SupplierRequestDTO dto) {
        Supplier supplier = supplierMapper.toEntity(dto);
        Supplier saved = supplierRepository.save(supplier);
        return supplierMapper.toResponseDto(saved);
    }

    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Fournisseur introuvable avec l'id : " + id));
        return supplierMapper.toResponseDto(supplier);
    }

    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO dto) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Fournisseur introuvable avec l'id : " + id));
        existing.setName(dto.getName());
        existing.setRating(dto.getRating());
        existing.setContact(dto.getContact());
        existing.setLeadTime(dto.getLeadTime());
        Supplier updated = supplierRepository.save(existing);
        return supplierMapper.toResponseDto(updated);
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new SupplierNotFoundException("Fournisseur introuvable avec l'id : " + id);
        }
        if (supplyOrderRepository.existsBySupplierId(id)) {
            throw new BusinessException("Impossible de supprimer le fournisseur. Il est associé à une ou plusieurs commandes.");
        }
        supplierRepository.deleteById(id);
    }

    public SupplierResponseDTO getSupplierByName(String name) {
        Supplier supplier = supplierRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with name: " + name));

        return supplierMapper.toResponseDto(supplier);
    }




}
