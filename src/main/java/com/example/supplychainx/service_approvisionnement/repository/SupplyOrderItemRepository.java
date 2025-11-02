package com.example.supplychainx.service_approvisionnement.repository;

import com.example.supplychainx.service_approvisionnement.model.SupplyOrderItem;
import com.example.supplychainx.service_production.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyOrderItemRepository extends BaseRepository<SupplyOrderItem, Long> {
    boolean existsByRawMaterialId(Long materialId);
}
