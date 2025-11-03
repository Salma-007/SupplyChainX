package com.example.supplychainx.service_delivery.repository;

import com.example.supplychainx.service_delivery.model.Customer;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer, Long>{
    Optional<Customer> findByNameIgnoreCase(String name);
}
