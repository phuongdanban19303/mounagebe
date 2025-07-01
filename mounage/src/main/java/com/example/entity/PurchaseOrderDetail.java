    package com.example.entity;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;
    import org.hibernate.annotations.ColumnDefault;

    import java.math.BigDecimal;
    import java.time.Instant;
    import java.time.LocalDate;

    @Getter
    @Setter
    @Entity
    @Table(name = "purchase_order_details")
    public class PurchaseOrderDetail {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "detail_id", nullable = false)
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "purchase_order_id", nullable = false)
        private PurchaseOrder purchaseOrder;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "product_id", nullable = false)
        private Product product;

        @Column(name = "quantity_ordered", nullable = false)
        private Integer quantityOrdered;

        @ColumnDefault("0")
        @Column(name = "quantity_received")
        private Integer quantityReceived;

        @Column(name = "unit_cost", nullable = false, precision = 12, scale = 2)
        private BigDecimal unitCost;

        @ColumnDefault("(`quantity_received` * `unit_cost`)")
        @Column(name = "total_cost", insertable = false, updatable = false)
        private BigDecimal totalCost;

        @Column(name = "batch_number", length = 50)
        private String batchNumber;

        @Column(name = "manufacturing_date")
        private LocalDate manufacturingDate;

        @Column(name = "expiry_date")
        private LocalDate expiryDate;

        @ColumnDefault("CURRENT_TIMESTAMP")
        @Column(name = "created_at")
        private Instant createdAt;

    }