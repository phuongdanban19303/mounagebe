package com.example.model.Invertory;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryResponse {
    private Integer productId;
    private String productName;
    private Integer quantityOnHand;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private LocalDate lastStocktakeDate;}
