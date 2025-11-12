package com.example.supplychainx.service_delivery.dto.order;

import com.example.supplychainx.service_delivery.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponseDTO {
    private Long id;

    private Long productId;
    private String productName;

    private Long customerId;
    private String customerName;

    private int quantity;
    private OrderStatus status;
}
