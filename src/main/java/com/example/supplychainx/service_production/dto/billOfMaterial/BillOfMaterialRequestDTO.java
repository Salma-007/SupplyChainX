package com.example.supplychainx.service_production.dto.billOfMaterial;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BillOfMaterialRequestDTO {

    @NotNull(message = "L'ID de la matière première est obligatoire.")
    private Long rawMaterialId;

    @NotNull(message = "La quantité est obligatoire.")
    @Min(value = 1, message = "La quantité doit être supérieure à zéro.")
    private Integer quantity;
}
