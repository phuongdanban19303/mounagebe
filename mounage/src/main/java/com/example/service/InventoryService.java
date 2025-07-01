package com.example.service;

import com.example.model.Invertory.ExpiringProductResponse;
import com.example.model.Invertory.InventoryResponse;
import com.example.model.Invertory.StockAdjustmentRequest;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getAllInventory();
    List<InventoryResponse> getInventoryReport();
    Boolean adjustStock(StockAdjustmentRequest request);
    List<ExpiringProductResponse> getExpiringProducts(int daysAhead);
}
