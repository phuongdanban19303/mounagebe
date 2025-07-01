package com.example.model.Invertory;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpiringProductResponse {
    private int productId;
    private String productName;
    private String batchNumber;
    private LocalDate expiryDate;
    private int quantityReceived;

    public ExpiringProductResponse(Integer productId, String productName, String batchNumber,
                                   LocalDate expiryDate, Integer quantityReceived) {
        this.productId = productId;
        this.productName = productName;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.quantityReceived = quantityReceived;
    }
}
