package com.example.supplychainx.service_delivery.dto.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequestDTO {
    @NotBlank(message = "Le nom du client est obligatoire.")
    private String name;

    @NotBlank(message = "L'adresse est obligatoire.")
    private String address;

    @NotBlank(message = "La ville est obligatoire.")
    private String city;
}
