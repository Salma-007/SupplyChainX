package com.example.supplychainx.service_approvisionnement.model;

import com.example.supplychainx.service_production.model.BillOfMaterial;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int stockMin;

    @Column(nullable = false)
    private String unit;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "supplier_rawmaterial",
            joinColumns = @JoinColumn(name = "rawmaterial_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private Set<Supplier> suppliers = new HashSet<>();

    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyOrderItem> orderItems;

    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillOfMaterial> bills;
}
