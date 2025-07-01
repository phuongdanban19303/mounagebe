package com.example.model.Sales;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleRequest {
    private CustomerRequest customer;
    private List<SaleItemRequest> items;
    private String paymentMethod;
    private BigDecimal cashReceived;
    private String notes;
    // getter/setter
}