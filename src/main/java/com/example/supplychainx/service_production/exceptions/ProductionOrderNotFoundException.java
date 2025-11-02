package com.example.supplychainx.service_production.exceptions;

public class ProductionOrderNotFoundException extends RuntimeException {
    public ProductionOrderNotFoundException(String message) {
        super(message);
    }
}
