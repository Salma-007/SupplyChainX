package com.example.supplychainx.service_delivery.dto.delivery;

import com.example.supplychainx.service_delivery.model.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResponseDTO {
    private Long id;
    private Long orderId;
    private Long orderCustomerId;

    private String vehicule;
    private DeliveryStatus status;
    private String driver;
    private LocalDate deliveryDate;
    private Double cost;

    private Double totalCost;
}
