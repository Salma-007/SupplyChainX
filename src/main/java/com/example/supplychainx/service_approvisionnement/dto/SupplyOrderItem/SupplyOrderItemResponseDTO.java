package com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem;

import lombok.Data;

@Data
public class SupplyOrderItemResponseDTO {

    private Long id;
    private Long materialId;
    private String materialName;
    private Integer quantity;
}
