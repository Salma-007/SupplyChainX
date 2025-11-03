package com.example.supplychainx.service_delivery.repository;

import com.example.supplychainx.service_delivery.model.Order;

public interface OrderRepository extends BaseRepository<Order, Long>{
    boolean existsByCustomer_Id(Long id);
}
