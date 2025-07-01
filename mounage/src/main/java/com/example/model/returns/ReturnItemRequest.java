package com.example.model.returns;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReturnItemRequest {
    private Integer originalDetailId;
    private String barcode; // thay vì productId
    private Integer quantityReturned;
    private BigDecimal unitPrice;
}
