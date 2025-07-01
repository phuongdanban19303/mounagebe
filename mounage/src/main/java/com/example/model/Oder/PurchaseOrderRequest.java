package com.example.model.Oder;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderRequest {
    private String purchaseOrderNumber;
    private Integer supplierId;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private LocalDate receivedDate;
    private String status;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private String notes;
    private Integer createdBy;
    private Integer receivedBy;
    private List<PurchaseOrderDetailRequest> details;
}
