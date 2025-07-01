package com.example.API;

import com.example.model.Oder.PurchaseOrderDetailRequest;
import com.example.model.Oder.PurchaseOrderDetailResponse;
import com.example.model.Oder.PurchaseOrderRequest;
import com.example.model.Oder.PurchaseOrderResponse;
import com.example.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class OderAPI {
    private final PurchaseOrderService service;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createOrder(@RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.ok(service.createPurchaseOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(service.getAllPurchaseOrders());
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<List<PurchaseOrderDetailResponse>> getOrderDetails(@PathVariable Integer orderId) {
        return ResponseEntity.ok(service.getOrderDetails(orderId));
    }
}
