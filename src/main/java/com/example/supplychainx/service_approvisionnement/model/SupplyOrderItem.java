package com.example.supplychainx.service_approvisionnement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "supply_order_item")
public class SupplyOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supply_order_id", nullable = false)
    private SupplyOrder supplyOrder;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(nullable = false)
    private Integer quantity;
}
