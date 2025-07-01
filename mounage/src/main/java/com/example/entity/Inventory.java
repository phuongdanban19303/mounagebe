package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private com.example.entity.Product product;

    @ColumnDefault("0")
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand;

    @ColumnDefault("0")
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @ColumnDefault("(`quantity_on_hand` - `reserved_quantity`)")
    @Column(name = "available_quantity", insertable = false, updatable = false)
    private Integer availableQuantity;
    @Column(name = "last_stocktake_date")
    private LocalDate lastStocktakeDate;

    @Column(name = "last_movement_date")
    private Instant lastMovementDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;
}