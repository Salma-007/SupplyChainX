package com.example.supplychainx.service_production.dto.product;

import lombok.Data;

@Data

public class ProductResponseDTO {
    private Long id;
    private String name;
    private int productionTime;
    private Double cost;
    private int stock;
}
