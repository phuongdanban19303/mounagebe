package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sale_details")
public class SaleDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false)
    private com.example.entity.Sale sale;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @ColumnDefault("0.00")
    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @ColumnDefault("0.00")
    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @ColumnDefault("((`quantity` * `unit_price`) - `discount_amount`)")
    @Column(name = "line_total", insertable = false, updatable = false)
    private BigDecimal lineTotal;

    @Column(name = "cost_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @ColumnDefault("(`line_total` - (`quantity` * `cost_price`))")
    @Column(name = "profit", insertable = false, updatable = false)
    private BigDecimal profit;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;

}