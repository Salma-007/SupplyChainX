package com.example.supplychainx.service_approvisionnement.mapper;

import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialResponseDTO;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    RawMaterial toEntity(RawMaterialRequestDTO dto);
    RawMaterialResponseDTO toResponseDto(RawMaterial rawMaterial);
}
