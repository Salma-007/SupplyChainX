package com.example.supplychainx.service_approvisionnement.mapper;

import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialResponseDTO;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierSimpleDTO;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_approvisionnement.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = SupplierMapper.class)
public interface RawMaterialMapper {

    RawMaterial toEntity(RawMaterialRequestDTO dto);

    @Mapping(target = "suppliers", source = "suppliers")
    RawMaterialResponseDTO toResponseDto(RawMaterial rawMaterial);

    default SupplierSimpleDTO toSupplierSimpleDTO(Supplier supplier) {
        if (supplier == null) return null;
        SupplierSimpleDTO dto = new SupplierSimpleDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        return dto;
    }

    default List<SupplierSimpleDTO> mapSuppliers(Set<Supplier> suppliers) {
        if (suppliers == null) return null;
        return suppliers.stream()
                .map(this::toSupplierSimpleDTO)
                .toList();
    }
}
