package com.example.model.returns;

import lombok.Data;

import java.util.List;

@Data
public class ReturnRequest {
    private String saleNumber;
    private Integer customerId;
    private String reason;
    private String refundMethod;
    private String notes;
    private List<ReturnItemRequest> items;
}