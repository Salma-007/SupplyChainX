package com.example.supplychainx.service_production.dto.billOfMaterial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillOfMaterialResponseDTO {

    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private Integer quantity;

    public BillOfMaterialResponseDTO(long l, String acier, Integer v) {
        this.rawMaterialId = l;
        this.rawMaterialName = acier;
        this.quantity = v;
    }
}