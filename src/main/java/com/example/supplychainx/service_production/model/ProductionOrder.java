package com.example.supplychainx.service_production.model;

import com.example.supplychainx.service_production.model.enums.ProductionOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @Column(nullable = false)
    private ProductionOrderStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
