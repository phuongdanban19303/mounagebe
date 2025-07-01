package com.example.model.Invertory;

import com.example.entity.Inventory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryResponse {
    private Integer productId;
    private String productName;
    private Integer quantityOnHand;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private LocalDate lastStocktakeDate;

    public static InventoryResponse fromEntity(Inventory entity) {
        InventoryResponse res = new InventoryResponse();
        res.setProductId(entity.getProduct().getId());
        res.setProductName(entity.getProduct().getProductName());
        res.setQuantityOnHand(entity.getQuantityOnHand());
        res.setReservedQuantity(entity.getReservedQuantity());
        res.setAvailableQuantity(entity.getAvailableQuantity());
        return res;
    }}
