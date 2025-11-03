package com.example.supplychainx.service_production.dto.product;

import com.example.supplychainx.service_production.dto.billOfMaterial.BillOfMaterialResponseDTO;
import com.example.supplychainx.service_production.model.BillOfMaterial;
import lombok.Data;

import java.util.List;

@Data

public class ProductResponseDTO {
    private Long id;
    private String name;
    private int productionTime;
    private Double cost;
    private int stock;
    private List<BillOfMaterialResponseDTO> bills;
}
