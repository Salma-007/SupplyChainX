package com.example.supplychainx.exceptionhandler;

import com.example.supplychainx.service_approvisionnement.exceptions.*;
import com.example.supplychainx.service_delivery.exceptions.CustomerNotFoundException;
import com.example.supplychainx.service_delivery.exceptions.DeliveryNotFoundException;
import com.example.supplychainx.service_delivery.exceptions.OrderNotFoundException;
import com.example.supplychainx.service_production.exceptions.ProductNotFoundException;
import com.example.supplychainx.service_production.exceptions.ProductionOrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            RawMaterialNotFoundException.class,
            SupplierNotFoundException.class,
            SupplyOrderNotFoundException.class,
            UserNotFoundException.class,
            OrderIsReceivedException.class,
            BusinessException.class,
            CustomerNotFoundException.class,
            DeliveryNotFoundException.class,
            OrderNotFoundException.class,
            ProductionOrderNotFoundException.class,
            ProductNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFoundExceptions(Exception ex, WebRequest request) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    private ResponseEntity<Object> buildErrorResponse(
            HttpStatus status, String message, String requestDescription) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (requestDescription.startsWith("uri=")) {
            body.put("path", requestDescription.substring(4));
        } else {
            body.put("path", requestDescription);
        }

        return new ResponseEntity<>(body, status);
    }

}
