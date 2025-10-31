package com.example.supplychainx.service_approvisionnement.mapper;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItemDTO;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplyOrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplyOrder", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    SupplyOrderItem toEntity(SupplyOrderItemDTO dto);

    @Mapping(target = "materialId", source = "rawMaterial.id")
    SupplyOrderItemDTO toResponseDto(SupplyOrderItem item);
}
