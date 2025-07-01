package com.example.model.Sales;

import java.math.BigDecimal;
import java.time.Instant;

public class InventoryReportResponse {
    private Integer productId;
    private String productName;
    private Integer quantityOnHand;
    private BigDecimal costPrice;
    private BigDecimal inventoryValue;
    private Instant lastMovementDate;

    public InventoryReportResponse(Integer productId, String productName, Integer quantityOnHand, BigDecimal costPrice, BigDecimal inventoryValue, Instant lastMovementDate) {
        this.productId = productId;
        this.productName = productName;
        this.quantityOnHand = quantityOnHand;
        this.costPrice = costPrice;
        this.inventoryValue = inventoryValue;
        this.lastMovementDate = lastMovementDate;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getInventoryValue() {
        return inventoryValue;
    }

    public void setInventoryValue(BigDecimal inventoryValue) {
        this.inventoryValue = inventoryValue;
    }

    public Instant getLastMovementDate() {
        return lastMovementDate;
    }

    public void setLastMovementDate(Instant lastMovementDate) {
        this.lastMovementDate = lastMovementDate;
    }
} 