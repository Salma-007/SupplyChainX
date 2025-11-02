package com.example.supplychainx.service_production.mapper;

import com.example.supplychainx.service_production.dto.product.ProductRequestDTO;
import com.example.supplychainx.service_production.dto.product.ProductResponseDTO;
import com.example.supplychainx.service_production.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequestDTO dto);
    ProductResponseDTO toResponseDto(Product product);
}
