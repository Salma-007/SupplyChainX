package com.example.supplychainx.service_approvisionnement.dto.SupplyOrder;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItemDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SupplyOrderRequestDTO {
    @NotNull(message = "L'ID du fournisseur est requis.")
    private Long supplierId;

    @NotNull(message = "La commande doit contenir au moins une ligne d'article.")
    @Size(min = 1, message = "La commande doit contenir au moins un article.")
    @Valid
    private List<SupplyOrderItemDTO> orderItems;
}
