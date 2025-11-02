package com.example.supplychainx.service_approvisionnement.repository;

import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_production.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RawMaterialRepository extends BaseRepository<RawMaterial, Long> {

    @Query("SELECT r FROM RawMaterial r WHERE r.stock <= r.stockMin")
    List<RawMaterial> findLowStockRawMaterials();

}
