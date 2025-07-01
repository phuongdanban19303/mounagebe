package com.example.model.Sales;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
@Data
public class SaleResponse {
    private Integer saleId;
    private String saleNumber;
    private String customerName;
    private Instant saleDate;
    private String cashierName;
    private BigDecimal subtotal;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private BigDecimal cashReceived;
    private String status;
    private String notes;
    private Instant createdAt;
    private BigDecimal discountAmount;
    private BigDecimal changeGiven;
    private List<SaleDetailResponse> saleDetail;
    // getter/setter
}