package com.example.supplychainx.service_approvisionnement.repository;

import com.example.supplychainx.service_approvisionnement.model.Supplier;
import com.example.supplychainx.service_production.repository.BaseRepository;

import java.util.Optional;

public interface SupplierRepository extends BaseRepository<Supplier,Long > {
    Optional<Supplier> findByNameIgnoreCase(String name);
}
