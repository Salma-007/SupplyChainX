package com.example.supplychainx.service_approvisionnement.mapper;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderResponseDTO;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplyOrderMapper {

    SupplyOrder toEntity(SupplyOrderRequestDTO dto);

    SupplyOrderResponseDTO toResponseDto(SupplyOrder supplyOrder);

    void updateSupplyOrderFromDto(SupplyOrderRequestDTO dto,
                                  @MappingTarget SupplyOrder order);
}
