package com.example.service.Impl;

import com.example.entity.Inventory;
import com.example.entity.Product;
import com.example.entity.PurchaseOrder;
import com.example.entity.PurchaseOrderDetail;
import com.example.entity.Supplier;
import com.example.model.Oder.PurchaseOrderDetailRequest;
import com.example.model.Oder.PurchaseOrderDetailResponse;
import com.example.model.Oder.PurchaseOrderRequest;
import com.example.model.Oder.PurchaseOrderResponse;
import com.example.model.UserUpdate;
import com.example.repository.InventoryRepository;
import com.example.repository.ProductRepository;
import com.example.repository.PurchaseOrderDetailRepository;
import com.example.repository.PurchaseOrderRepository;
import com.example.repository.SupplierRepository;
import com.example.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepo;
    private final PurchaseOrderDetailRepository detailRepo;
    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;
    private final SupplierRepository supplierRepo;
    private final ModelMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;

    private String generateOrderNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String timestamp = java.time.LocalDateTime.now().format(formatter);
        int random = new Random().nextInt(900) + 100; // random 3 số
        return "PO-" + timestamp + "-" + random;
    }

    @Override
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        long supplierId = request.getSupplierId() != null ? request.getSupplierId() : 1;
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp mặc định"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserUpdate userDetails = (UserUpdate) authentication.getPrincipal();

        // 1. Map và lưu thông tin đơn hàng chính
        PurchaseOrder order = mapper.map(request, PurchaseOrder.class);
        order.setSupplier(supplier);
        order.setCreatedBy(userDetails.getUser().getId());
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            order.setStatus("received");
        }
        // Sinh mã đơn hàng nếu chưa có
        if (request.getPurchaseOrderNumber() == null || request.getPurchaseOrderNumber().isBlank()) {
            order.setPurchaseOrderNumber(generateOrderNumber());
        } else {
            order.setPurchaseOrderNumber(request.getPurchaseOrderNumber());
        }
        // Tạm thời chưa save, để set details trước
        
        // 2. Xử lý danh sách chi tiết đơn hàng
        if (request.getDetails() == null || request.getDetails().isEmpty()) {
            throw new IllegalArgumentException("Purchase order must have at least one detail line.");
        }

        List<PurchaseOrderDetail> detailEntities = new ArrayList<>();
        for (PurchaseOrderDetailRequest detailDto : request.getDetails()) {
            Product product = productRepo.findByBarcode(detailDto.getBarcode())
                    .orElseThrow(() -> new RuntimeException("Product not found with barcode: " + detailDto.getBarcode()));
            
            PurchaseOrderDetail detail = mapper.map(detailDto, PurchaseOrderDetail.class);
            detail.setPurchaseOrder(order); // Gán đơn hàng chính cho chi tiết
            detail.setProduct(product);
            detail.setCreatedAt(Instant.now());
            // Đảm bảo quantityReceived không null
            if (detail.getQuantityReceived() == null) {
                detail.setQuantityReceived(0);
            }
            detailEntities.add(detail);
        }

        // 3. Gán danh sách chi tiết vào đơn hàng và lưu
        order.setPurchaseOrderDetails(new ArrayList<>(detailEntities));
        PurchaseOrder savedOrder = purchaseOrderRepo.save(order);
        
        // CẬP NHẬT TỒN KHO NGAY LẬP TỨC
        for (PurchaseOrderDetail d : savedOrder.getPurchaseOrderDetails()) {
            entityManager.refresh(d); // Lấy giá trị generated từ DB
            
            // Lấy số lượng để cập nhật (ưu tiên quantityReceived, nếu không có thì lấy quantity)
            int quantityToAdd = (d.getQuantityReceived() != null && d.getQuantityReceived() > 0) 
                                ? d.getQuantityReceived() 
                                : d.getQuantityOrdered();

            if (quantityToAdd > 0) {
                 Inventory inventory = inventoryRepo.findByProduct(d.getProduct())
                        .orElseGet(() -> {
                            Inventory newInventory = new Inventory();
                            newInventory.setProduct(d.getProduct());
                            newInventory.setQuantityOnHand(0);
                            newInventory.setReservedQuantity(0);
                            newInventory.setCreatedAt(Instant.now());
                            return newInventory; 
                        });
                int newQty = inventory.getQuantityOnHand() + quantityToAdd;
                inventory.setQuantityOnHand(newQty);
                inventory.setLastMovementDate(Instant.now());
                inventoryRepo.save(inventory);
            }
        }

        // 4. Tính toán tổng tiền và cập nhật lại đơn hàng
        BigDecimal totalAmount = savedOrder.getPurchaseOrderDetails().stream()
                .map(d -> d.getTotalCost() != null ? d.getTotalCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        savedOrder.setTotalAmount(totalAmount);
        purchaseOrderRepo.save(savedOrder);

        PurchaseOrderResponse response = mapper.map(savedOrder, PurchaseOrderResponse.class);
        return response;
    }

    @Override
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepo.findAllByOrderByCreatedAtDesc().stream()
                .map(o -> {
                    PurchaseOrderResponse dto = mapper.map(o, PurchaseOrderResponse.class);
                    // Chuyển đổi thủ công Set -> List cho details
                    return dto;
                })
                .toList();
    }

    @Override
    public List<PurchaseOrderDetailResponse> getOrderDetails(Integer purchaseOrderId) {
        return detailRepo.findByPurchaseOrderId(purchaseOrderId).stream()
                .map(d -> mapper.map(d, PurchaseOrderDetailResponse.class))
                .toList();
    }
}
