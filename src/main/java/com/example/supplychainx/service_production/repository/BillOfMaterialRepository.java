package com.example.supplychainx.service_production.repository;

import com.example.supplychainx.service_production.model.BillOfMaterial;

import java.util.List;

public interface BillOfMaterialRepository extends BaseRepository<BillOfMaterial, Long>{
    List<BillOfMaterial> findByProductId(Long id);
}
