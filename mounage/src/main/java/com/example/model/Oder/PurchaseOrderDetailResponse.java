package com.example.model.Oder;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseOrderDetailResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer quantityOrdered;
    private Integer quantityReceived;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String batchNumber;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
}