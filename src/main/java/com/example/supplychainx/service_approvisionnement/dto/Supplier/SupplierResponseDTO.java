package com.example.supplychainx.service_approvisionnement.dto.Supplier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String contact;
    private Double rating;
    private int leadTime;
}
