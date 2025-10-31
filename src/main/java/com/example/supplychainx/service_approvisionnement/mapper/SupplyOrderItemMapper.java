package com.example.supplychainx.service_approvisionnement.mapper;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem.SupplyOrderItemDTO;
import com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem.SupplyOrderItemResponseDTO;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplyOrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplyOrder", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    SupplyOrderItem toEntity(SupplyOrderItemDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "materialId", source = "rawMaterial.id")
    @Mapping(target = "materialName", source = "rawMaterial.name")
    @Mapping(target = "quantity", source = "quantity")
    SupplyOrderItemResponseDTO toResponseDto(SupplyOrderItem item);
}
