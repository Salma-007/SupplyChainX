package com.example.supplychainx.service_production.dto.billOfMaterial;

import lombok.Data;

@Data
public class BillOfMaterialResponseDTO {

    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private Integer quantity;
}