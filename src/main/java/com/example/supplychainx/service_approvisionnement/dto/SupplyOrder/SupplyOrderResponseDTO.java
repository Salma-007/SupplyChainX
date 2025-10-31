package com.example.supplychainx.service_approvisionnement.dto.SupplyOrder;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem.SupplyOrderItemResponseDTO;
import com.example.supplychainx.service_approvisionnement.model.enums.SupplyOrderStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SupplyOrderResponseDTO {
    private Long id;
    private LocalDate orderdate;
    private Long supplierId;
    private SupplyOrderStatus status;
    private List<SupplyOrderItemResponseDTO> orderItems;
}

