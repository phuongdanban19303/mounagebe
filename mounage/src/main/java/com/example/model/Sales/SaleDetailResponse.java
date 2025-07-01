package com.example.model.Sales;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class SaleDetailResponse {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal lineTotal;
    private Instant createdAt;
    // getter/setter
} 