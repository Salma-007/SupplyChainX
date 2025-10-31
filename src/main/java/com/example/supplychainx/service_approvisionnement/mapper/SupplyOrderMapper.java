package com.example.supplychainx.service_approvisionnement.mapper;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderResponseDTO;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SupplyOrderItemMapper.class})
public interface SupplyOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderdate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "orderItems", source = "orderItems")
    SupplyOrder toEntity(SupplyOrderRequestDTO dto);

    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "status", source = "status")
    SupplyOrderResponseDTO toResponseDto(SupplyOrder supplyOrder);

}
