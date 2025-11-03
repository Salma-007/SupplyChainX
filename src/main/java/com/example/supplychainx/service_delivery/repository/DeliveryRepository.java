package com.example.supplychainx.service_delivery.repository;

import com.example.supplychainx.service_delivery.model.Delivery;

public interface DeliveryRepository extends BaseRepository<Delivery, Long>{
    boolean existsByOrder_Id(Long id);
}
