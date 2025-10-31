package com.example.supplychainx.service_approvisionnement.repository;

import com.example.supplychainx.service_approvisionnement.model.SupplyOrder;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplyOrderRepository extends BaseRepository<SupplyOrder, Long>{
    @Query("SELECT DISTINCT o FROM SupplyOrder o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.rawMaterial rm " +
            "JOIN FETCH o.supplier s")
    List<SupplyOrder> findAllWithFullDetails();
}
