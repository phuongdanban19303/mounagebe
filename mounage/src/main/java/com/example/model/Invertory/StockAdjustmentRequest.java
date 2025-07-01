package com.example.model.Invertory;

import lombok.Data;


@Data
public class StockAdjustmentRequest {
    private int productId;
    private int actualQuantity;
}