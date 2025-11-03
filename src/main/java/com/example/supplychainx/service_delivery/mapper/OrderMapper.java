package com.example.supplychainx.service_delivery.mapper;

import com.example.supplychainx.service_delivery.dto.order.OrderRequestDTO;
import com.example.supplychainx.service_delivery.dto.order.OrderResponseDTO;
import com.example.supplychainx.service_delivery.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    OrderResponseDTO toResponseDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Order toEntity(OrderRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "customer", ignore = true)
    void updateOrderFromDto(OrderRequestDTO dto, @MappingTarget Order target);
}
