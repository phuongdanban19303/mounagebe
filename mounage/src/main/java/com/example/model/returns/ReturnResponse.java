package com.example.model.returns;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ReturnResponse {
    private Integer id;
    private String returnNumber;
    private Integer customerId;
    private Integer processedBy;
    private String reason;
    private BigDecimal returnAmount;
    private String refundMethod;
    private String status;
    private String notes;
    private Instant returnDate;
}
