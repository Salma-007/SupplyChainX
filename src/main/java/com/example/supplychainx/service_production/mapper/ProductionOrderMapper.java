package com.example.supplychainx.service_production.mapper;


import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderRequestDTO;
import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderResponseDTO;
import com.example.supplychainx.service_production.model.ProductionOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductionOrderMapper {
    @Mapping(target = "product", ignore = true)
    ProductionOrder toEntity(ProductionOrderRequestDTO dto);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    ProductionOrderResponseDTO toResponseDto(ProductionOrder entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntityFromDto(ProductionOrderRequestDTO dto, @MappingTarget ProductionOrder entity);

}
