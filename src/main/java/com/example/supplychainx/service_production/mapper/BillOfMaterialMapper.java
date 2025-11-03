package com.example.supplychainx.service_production.mapper;

import com.example.supplychainx.service_production.dto.billOfMaterial.BillOfMaterialRequestDTO;
import com.example.supplychainx.service_production.dto.billOfMaterial.BillOfMaterialResponseDTO;
import com.example.supplychainx.service_production.model.BillOfMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BillOfMaterialMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "rawMaterial.id", target = "rawMaterialId")
    @Mapping(source = "rawMaterial.name", target = "rawMaterialName")
    BillOfMaterialResponseDTO toResponseDto(BillOfMaterial billOfMaterial);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    @Mapping(source = "quantity", target = "quantity")
    BillOfMaterial toEntity(BillOfMaterialRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    void updateBillOfMaterialFromDto(BillOfMaterialRequestDTO dto, @MappingTarget BillOfMaterial target);
}
