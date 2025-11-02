package com.example.supplychainx.service_production.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "Le nom du produit est obligatoire.")
    private String name;

    @Min(value = 0, message = "Le temps de production ne peut être négatif.")
    private int productionTime;

    @NotNull(message = "Le coût est obligatoire.")
    @Min(value = 0, message = "Le coût ne peut être négatif.")
    private Double cost;

    @Min(value = 0, message = "Le stock ne peut être négatif.")
    private int stock;
}
