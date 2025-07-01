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
@Table(name = "returns")
public class Return {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id", nullable = false)
    private Integer id;

    @Column(name = "return_number", nullable = false, length = 50)
    private String returnNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "original_sale_id", nullable = false)
    private com.example.entity.Sale originalSale;

    @Column(name = "customer_id")
    private Integer customerId;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "return_date", nullable = false)
    private Instant returnDate;

    @Column(name = "processed_by", nullable = false)
    private Integer processedBy;

    @Lob
    @Column(name = "reason", nullable = false)
    private String reason;

    @ColumnDefault("0.00")
    @Column(name = "return_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal returnAmount;

    @Lob
    @Column(name = "refund_method", nullable = false)
    private String refundMethod;

    @ColumnDefault("'processed'")
    @Lob
    @Column(name = "status")
    private String status;

    @Lob
    @Column(name = "notes")
    private String notes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "returnField")
    private Set<ReturnDetail> returnDetails = new LinkedHashSet<>();

}