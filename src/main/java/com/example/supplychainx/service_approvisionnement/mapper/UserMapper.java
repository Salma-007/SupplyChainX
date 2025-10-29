package com.example.supplychainx.service_approvisionnement.mapper;

import com.example.supplychainx.service_approvisionnement.dto.User.UserRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.User.UserResponseDTO;
import com.example.supplychainx.service_approvisionnement.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDTO dto);
    UserResponseDTO toResponseDto(User user);
}
