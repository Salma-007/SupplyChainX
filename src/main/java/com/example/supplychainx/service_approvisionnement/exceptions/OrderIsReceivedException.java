package com.example.supplychainx.service_approvisionnement.exceptions;

public class OrderIsReceivedException extends RuntimeException {
    public OrderIsReceivedException(String message) {
        super(message);
    }
}
