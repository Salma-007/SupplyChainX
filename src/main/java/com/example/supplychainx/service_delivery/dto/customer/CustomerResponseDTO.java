package com.example.supplychainx.service_delivery.dto.customer;

import com.example.supplychainx.service_delivery.dto.order.OrderResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String city;

    private List<OrderResponseDTO> orders;
}
