package com.example.supplychainx.service_approvisionnement.dto.SupplyOrder;

import com.example.supplychainx.service_approvisionnement.model.enums.SupplyOrderStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class SupplyOrderResponseDTO {
    private Long id;
    private LocalDate orderdate;
    private SupplyOrderStatus status;
}
