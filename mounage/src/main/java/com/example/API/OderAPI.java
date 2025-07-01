package com.example.API;

import com.example.model.Oder.PurchaseOrderDetailRequest;
import com.example.model.Oder.PurchaseOrderDetailResponse;
import com.example.model.Oder.PurchaseOrderRequest;
import com.example.model.Oder.PurchaseOrderResponse;
import com.example.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    // ✅ API cập nhật đơn hàng
    @PutMapping("/{id}/update")
    public ResponseEntity<PurchaseOrderResponse> updatePurchaseOrder(
            @PathVariable("id") Integer id,
            @RequestBody PurchaseOrderRequest request
    ) {
        PurchaseOrderResponse updated = service.updatePurchaseOrder(id, request);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deletePurchaseOrder(@PathVariable("id") Integer id) {

        service.deletePurchaseOrderById(id);
        return ResponseEntity.ok("Xóa đơn hàng thành công.");
    }
}
