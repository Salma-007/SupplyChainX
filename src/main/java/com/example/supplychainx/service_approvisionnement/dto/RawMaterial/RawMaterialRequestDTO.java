package com.example.supplychainx.service_approvisionnement.dto.RawMaterial;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialRequestDTO {

    private String name;
    private int stock;

    @Min(value = 1, message = "La note doit être >= 0")
    private int stockMin;
    private String unit;

    private List<Long> supplierIds;
}
