package com.example.supplychainx.service_delivery.model;

import com.example.supplychainx.service_delivery.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String vehicule;

    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private String driver;

    @Column(nullable = false)
    private LocalDate deliveryDate;

    @Column(nullable = false)
    private Double cost;

}
