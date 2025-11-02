package com.example.supplychainx.service_production.dto.productionOrder;

import com.example.supplychainx.service_production.model.enums.ProductionOrderStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductionOrderResponseDTO {
    private Long id;
    private Integer quantity;
    private ProductionOrderStatus status;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long productId;
    private String productName;
}
