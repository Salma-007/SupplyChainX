package com.example.supplychainx.service_approvisionnement.exceptions;

public class SupplyOrderNotFoundException extends RuntimeException {
    public SupplyOrderNotFoundException(String message) {
        super(message);
    }
}
