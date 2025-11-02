package com.example.supplychainx.service_production.repository;

import com.example.supplychainx.service_production.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends BaseRepository<Product, Long>{

    List<Product> findByNameContainingIgnoreCase(String name);
    Optional<Product> findById(Long id);
}
