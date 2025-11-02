package com.example.supplychainx.service_production.repository;

import com.example.supplychainx.service_production.model.ProductionOrder;

public interface ProductionOrderRepository extends BaseRepository<ProductionOrder, Long>{
    boolean existsByProductId(Long productId);
}
