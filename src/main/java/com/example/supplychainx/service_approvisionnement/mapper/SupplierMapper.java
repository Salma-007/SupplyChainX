package com.example.supplychainx.service_approvisionnement.mapper;

import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierResponseDTO;
import com.example.supplychainx.service_approvisionnement.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(SupplierRequestDTO dto);
    SupplierResponseDTO toResponseDto(Supplier supplier);

    void updateSupplierFromDto(SupplierRequestDTO dto, @MappingTarget Supplier supplier);
}
