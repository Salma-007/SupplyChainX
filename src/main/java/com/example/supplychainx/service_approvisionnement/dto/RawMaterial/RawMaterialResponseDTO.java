package com.example.supplychainx.service_approvisionnement.dto.RawMaterial;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawMaterialResponseDTO {
    private Long id;
    private String name;
    private int stock;
    private int stockMin;
    private String unit;
}
