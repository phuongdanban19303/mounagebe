package com.example.model.Sales;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SaleItemRequest {
    private Integer productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPercent;
    private BigDecimal costPrice; // optional
    // getter/setter
}