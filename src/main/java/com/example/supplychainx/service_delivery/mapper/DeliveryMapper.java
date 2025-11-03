package com.example.supplychainx.service_delivery.mapper;

import com.example.supplychainx.service_delivery.dto.delivery.DeliveryRequestDTO;
import com.example.supplychainx.service_delivery.dto.delivery.DeliveryResponseDTO;
import com.example.supplychainx.service_delivery.model.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.customer.id", target = "orderCustomerId")
    DeliveryResponseDTO toResponseDto(Delivery delivery);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    Delivery toEntity(DeliveryRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    void updateDeliveryFromDto(DeliveryRequestDTO dto, @MappingTarget Delivery target);
}
