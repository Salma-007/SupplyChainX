package com.example.supplychainx.service_raw_material.controller.dto;

import com.example.supplychainx.service_raw_material.controller.dto.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
