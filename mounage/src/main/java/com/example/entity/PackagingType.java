package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "packaging_types")
public class PackagingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "packaging_id", nullable = false)
    private Integer id;

    @Column(name = "packaging_code", nullable = false, length = 20)
    private String packagingCode;

    @Column(name = "packaging_name", nullable = false, length = 100)
    private String packagingName;

    @Lob
    @Column(name = "description")
    private String description;

    @ColumnDefault("1.00")
    @Column(name = "conversion_factor", precision = 10, scale = 2)
    private BigDecimal conversionFactor;

    @Column(name = "weight_per_unit", precision = 10, scale = 2)
    private BigDecimal weightPerUnit;

    @Column(name = "volume_per_unit", precision = 10, scale = 2)
    private BigDecimal volumePerUnit;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "packaging")
    private Set<com.example.entity.Product> products = new LinkedHashSet<>();

}