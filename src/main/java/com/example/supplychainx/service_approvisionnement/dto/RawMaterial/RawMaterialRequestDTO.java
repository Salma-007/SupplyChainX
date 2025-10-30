package com.example.supplychainx.service_approvisionnement.dto.RawMaterial;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawMaterialRequestDTO {

    private String name;
    private int stock;

    @Min(value = 1, message = "La note doit être >= 0")
    private int stockMin;
    private String unit;
}
