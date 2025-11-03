package com.example.supplychainx.service_production.mapper;

import com.example.supplychainx.service_production.dto.product.ProductRequestDTO;
import com.example.supplychainx.service_production.dto.product.ProductResponseDTO;
import com.example.supplychainx.service_production.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BillOfMaterialMapper.class})
public interface ProductMapper {


    Product toEntity(ProductRequestDTO dto);

    @Mapping(target = "bills", source = "bills")
    ProductResponseDTO toResponseDto(Product product);
}
