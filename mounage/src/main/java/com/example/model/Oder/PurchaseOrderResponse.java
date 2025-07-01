package com.example.model.Oder;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class PurchaseOrderResponse {
    private Integer id;
    private String purchaseOrderNumber;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private LocalDate receivedDate;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private String notes;
    private Integer createdBy;
    private Integer receivedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
