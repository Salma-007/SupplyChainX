package com.example.supplychainx.service_delivery.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequestDTO {
    @NotNull(message = "L'ID du produit est obligatoire.")
    private Long productId;

    @NotNull(message = "L'ID du client est obligatoire.")
    private Long customerId;

    @NotNull(message = "La quantité est obligatoire.")
    @Min(value = 1, message = "La quantité commandée doit être supérieure à zéro.")
    private int quantity;
}
