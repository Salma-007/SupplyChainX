package com.example.supplychainx.service_approvisionnement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private int leadTime;

    @ManyToMany(mappedBy = "suppliers")
    private Set<RawMaterial> rawMaterials;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyOrder> orders;

}
