package com.example.supplychainx.service_approvisionnement.dto.User;

import com.example.supplychainx.service_approvisionnement.model.enums.Role;
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
