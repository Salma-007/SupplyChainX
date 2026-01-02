package com.example.supplychainx.service_production.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello!");
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> helloAdmin() {
        return ResponseEntity.ok("Hello From Admin!");
    }
}
