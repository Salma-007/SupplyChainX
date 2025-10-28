package com.example.supplychainx.service_raw_material.controller.dto;

import com.example.supplychainx.service_raw_material.controller.dto.model.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    @Email(message = "Format d'email invalide")
    private String password;
    private Role role;
}
