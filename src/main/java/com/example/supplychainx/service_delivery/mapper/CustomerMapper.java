package com.example.supplychainx.service_delivery.mapper;

import com.example.supplychainx.service_delivery.dto.customer.CustomerRequestDTO;
import com.example.supplychainx.service_delivery.dto.customer.CustomerResponseDTO;
import com.example.supplychainx.service_delivery.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Customer toEntity(CustomerRequestDTO dto);

    CustomerResponseDTO toResponseDto(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateCustomerFromDto(CustomerRequestDTO dto, @MappingTarget Customer target);
}
