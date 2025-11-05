package com.example.supplychainx.service_approvisionnement.dto.RawMaterial;

import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierResponseDTO;
import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierSimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
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
