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
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id", nullable = false)
    private Integer id;

    @Column(name = "sale_number", nullable = false, length = 50)
    private String saleNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "sale_date", nullable = false)
    private Instant saleDate;

    @Column(name = "cashier_id", nullable = false)
    private Integer cashierId;

    @ColumnDefault("0.00")
    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @ColumnDefault("0.00")
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @ColumnDefault("0.00")
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @ColumnDefault("0.00")
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @ColumnDefault("'cash'")
    @Lob
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @ColumnDefault("0.00")
    @Column(name = "cash_received", precision = 15, scale = 2)
    private BigDecimal cashReceived;

    @ColumnDefault("0.00")
    @Column(name = "change_given", precision = 15, scale = 2)
    private BigDecimal changeGiven;

    @ColumnDefault("'completed'")
    @Lob
    @Column(name = "status")
    private String status;

    @ColumnDefault("0")
    @Column(name = "loyalty_points_earned")
    private Integer loyaltyPointsEarned;

    @ColumnDefault("0")
    @Column(name = "loyalty_points_used")
    private Integer loyaltyPointsUsed;

    @Lob
    @Column(name = "notes")
    private String notes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "originalSale")
    private Set<Return> returns = new LinkedHashSet<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SaleDetail> saleDetails = new LinkedHashSet<>();

}