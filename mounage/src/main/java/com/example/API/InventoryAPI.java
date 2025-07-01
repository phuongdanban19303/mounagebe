package com.example.API;

import com.example.model.Invertory.ExpiringProductResponse;
import com.example.model.Invertory.InventoryResponse;
import com.example.model.Invertory.StockAdjustmentRequest;
import com.example.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryAPI {

    private final InventoryService inventoryService;

    @GetMapping("/current")
    public ResponseEntity<List<InventoryResponse>> getCurrentInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getInventoryReport());
    }

    @PostMapping("/adjust")
    public ResponseEntity<Void> adjustStock(@RequestBody StockAdjustmentRequest request) {
        inventoryService.adjustStock(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<ExpiringProductResponse>> getExpiringProducts(
            @RequestParam(name = "days", required = false, defaultValue = "7") int daysAhead) {
        return ResponseEntity.ok(inventoryService.getExpiringProducts(daysAhead));
    }
}
