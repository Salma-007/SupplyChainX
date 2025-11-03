package com.example.supplychainx.service_production.mapper;

import com.example.supplychainx.service_approvisionnement.mapper.RawMaterialMapper;
import com.example.supplychainx.service_approvisionnement.mapper.SupplyOrderItemMapper;
import com.example.supplychainx.service_production.dto.billOfMaterial.BillOfMaterialRequestDTO;
import com.example.supplychainx.service_production.dto.billOfMaterial.BillOfMaterialResponseDTO;
import com.example.supplychainx.service_production.model.BillOfMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RawMaterialMapper.class})
public interface BillOfMaterialMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "rawMaterialId", source = "rawMaterial.id")
    @Mapping(target = "rawMaterialName", source = "rawMaterial.name")
    @Mapping(target = "quantity", source = "quantity")
    BillOfMaterialResponseDTO toResponseDto(BillOfMaterial billOfMaterial);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    @Mapping(source = "quantity", target = "quantity")
    BillOfMaterial toEntity(BillOfMaterialRequestDTO dto);

}
