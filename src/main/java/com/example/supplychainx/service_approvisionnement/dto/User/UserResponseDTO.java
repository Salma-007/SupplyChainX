package com.example.supplychainx.service_approvisionnement.dto.User;

import com.example.supplychainx.service_approvisionnement.model.enums.Role;
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
