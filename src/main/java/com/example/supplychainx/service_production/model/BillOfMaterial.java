package com.example.supplychainx.service_production.model;

import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BillOfMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(nullable = false)
    private Integer quantity;

    @Override
    public String toString() {
        return "BillOfMaterial{" +
                "id=" + id +
                ", rawMaterial=" + rawMaterial.getId() +
                ", quantity=" + quantity +
                '}';
    }
}
