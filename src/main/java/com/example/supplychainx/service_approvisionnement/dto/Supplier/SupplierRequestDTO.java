package com.example.supplychainx.service_approvisionnement.dto.Supplier;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SupplierRequestDTO {

    private String name;
    private String contact;

    @DecimalMin(value = "0.0", inclusive = true, message = "La note doit être >= 0")
    @DecimalMax(value = "5.0", inclusive = true, message = "La note doit être <= 5")
    private Double rating;

    private int leadTime;
}
