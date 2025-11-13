package com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplyOrderItemDTO {
    @NotNull(message = "L'ID de la matière première est requis.")
    private Long materialId;

    @NotNull(message = "La quantité est requise.")
    @Min(value = 1, message = "La quantité doit être positive.")
    private Integer quantity;
}
