package com.example.supplychainx.service_production.dto.productionOrder;

import com.example.supplychainx.service_production.model.enums.ProductionOrderStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductionOrderRequestDTO {
    @NotNull(message = "La quantité est requise.")
    @Min(value = 1, message = "La quantité doit être supérieure à zéro.")
    private Integer quantity;

    private ProductionOrderStatus status;

    @NotNull(message = "La date de début est requise.")
    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé.")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est requise.")
    private LocalDate endDate;

    @NotNull(message = "L'ID du produit est requis.")
    private Long productId;
}
