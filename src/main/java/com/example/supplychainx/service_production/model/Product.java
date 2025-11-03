package com.example.supplychainx.service_production.model;

import com.example.supplychainx.service_production.dto.billOfMaterial.BillOfMaterialRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Product {
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", productionTime=" + productionTime +
                ", cost=" + cost +
                ", stock=" + stock +
                ", bills=" + bills +
                ", orders=" + orders +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int productionTime;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private int stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillOfMaterial> bills;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionOrder> orders;

}
