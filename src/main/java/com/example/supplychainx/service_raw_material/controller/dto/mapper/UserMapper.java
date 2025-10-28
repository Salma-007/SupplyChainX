package com.example.supplychainx.service_raw_material.controller.dto.mapper;

import com.example.supplychainx.service_raw_material.controller.dto.UserRequestDTO;
import com.example.supplychainx.service_raw_material.controller.dto.UserResponseDTO;
import com.example.supplychainx.service_raw_material.controller.dto.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User toEntity(UserRequestDTO dto);
    @Mapping(target = "password", ignore = true)
    UserResponseDTO toResponseDto(User user);
}
