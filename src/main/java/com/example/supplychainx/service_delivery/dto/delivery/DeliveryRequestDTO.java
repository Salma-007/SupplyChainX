package com.example.supplychainx.service_delivery.dto.delivery;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequestDTO {
    @NotNull(message = "L'ID de la commande est obligatoire.")
    private Long orderId;

    @NotBlank(message = "Le véhicule est obligatoire.")
    private String vehicule;

    @NotBlank(message = "Le nom du chauffeur est obligatoire.")
    private String driver;

    @NotNull(message = "La date de livraison est obligatoire.")
    @FutureOrPresent(message = "La date de livraison ne peut être future.")
    private LocalDate deliveryDate;

    @NotNull(message = "Le coût de livraison est obligatoire.")
    private Double cost;
}
