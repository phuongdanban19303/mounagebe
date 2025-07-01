package com.example.service.Impl;

import com.example.entity.Inventory;
import com.example.model.Invertory.ExpiringProductResponse;
import com.example.model.Invertory.InventoryResponse;
import com.example.model.Invertory.StockAdjustmentRequest;
import com.example.repository.InventoryRepository;
import com.example.repository.ProductRepository;
import com.example.repository.PurchaseOrderDetailRepository;
import com.example.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final PurchaseOrderDetailRepository podRepo;
    @Override
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepo.findAll().stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getInventoryReport() {
        int threshold = 10; // Ngưỡng cảnh báo tồn kho thấp - có thể cho vào cấu hình sau này

        return inventoryRepo.findAll().stream()
                .filter(inv -> inv.getAvailableQuantity() != null && inv.getAvailableQuantity() < threshold)
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean adjustStock(StockAdjustmentRequest request) {
        Inventory inventory = inventoryRepo.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setQuantityOnHand(request.getActualQuantity());
        inventory.setLastStocktakeDate(LocalDate.now());
        inventoryRepo.save(inventory);
        return true;
    }

    @Override
    public List<ExpiringProductResponse> getExpiringProducts(int daysAhead) {
        LocalDate targetDate = LocalDate.now().plusDays(daysAhead);
        return podRepo.findExpiringProducts(targetDate);
    }
}
