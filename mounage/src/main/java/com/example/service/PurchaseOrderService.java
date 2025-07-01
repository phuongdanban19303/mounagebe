package com.example.service;

import com.example.model.Oder.PurchaseOrderDetailRequest;
import com.example.model.Oder.PurchaseOrderDetailResponse;
import com.example.model.Oder.PurchaseOrderRequest;
import com.example.model.Oder.PurchaseOrderResponse;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request);
    // void confirmPurchaseOrder(Integer purchaseOrderId); // Đã gộp vào create
    List<PurchaseOrderResponse> getAllPurchaseOrders();
    List<PurchaseOrderDetailResponse> getOrderDetails(Integer purchaseOrderId);
}
