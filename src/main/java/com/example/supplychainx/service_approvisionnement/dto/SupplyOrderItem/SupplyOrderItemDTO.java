package com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SupplyOrderItemDTO {
    @NotNull(message = "L'ID de la matière première est requis.")
    private Long materialId;

    @NotNull(message = "La quantité est requise.")
    @Min(value = 1, message = "La quantité doit être positive.")
    private Integer quantity;
}
