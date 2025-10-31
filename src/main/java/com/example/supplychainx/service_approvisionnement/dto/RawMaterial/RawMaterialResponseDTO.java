package com.example.supplychainx.service_approvisionnement.dto.RawMaterial;

import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierResponseDTO;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierSimpleDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RawMaterialResponseDTO {
    private Long id;
    private String name;
    private int stock;
    private int stockMin;
    private String unit;

    private List<SupplierSimpleDTO> suppliers;
}
