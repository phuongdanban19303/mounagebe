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

        // 2. Xử lý danh sách chi tiết đơn hàng
        if (request.getDetails() == null || request.getDetails().isEmpty()) {
            throw new IllegalArgumentException("Purchase order must have at least one detail line.");
        }

        List<PurchaseOrderDetail> detailEntities = new ArrayList<>();

        for (PurchaseOrderDetailRequest detailDto : request.getDetails()) {
            // Tìm sản phẩm theo barcode
            Product product = productRepo.findByBarcode(detailDto.getBarcode())
                    .orElseThrow(() -> new RuntimeException("Product not found with barcode: " + detailDto.getBarcode()));

            // Map DTO sang entity
            PurchaseOrderDetail detail = mapper.map(detailDto, PurchaseOrderDetail.class);
            detail.setPurchaseOrder(order);
            detail.setProduct(product);
            detail.setCreatedAt(Instant.now());

            // Đảm bảo quantityReceived không null
            if (detail.getQuantityReceived() == null) {
                detail.setQuantityReceived(0);
            }

            // ✅ Tính toán totalCost = quantityOrdered * unitCost
            Integer quantityOrdered = detail.getQuantityOrdered();
            BigDecimal unitCost = detail.getUnitCost();

            if (quantityOrdered == null || unitCost == null) {
                throw new IllegalArgumentException("Missing quantityOrdered or unitCost for product: " + detail.getProduct().getBarcode());
            }
            if (quantityOrdered < 0) {
                throw new IllegalArgumentException("Quantity ordered cannot be negative for product: " + detail.getProduct().getBarcode());
            }
            if (unitCost.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Unit cost cannot be negative for product: " + detail.getProduct().getBarcode());
            }

            BigDecimal totalCost = unitCost.multiply(BigDecimal.valueOf(quantityOrdered));
            detail.setTotalCost(totalCost);

            detailEntities.add(detail);
        }

        // 3. Gán danh sách chi tiết vào đơn hàng và lưu
        order.setPurchaseOrderDetails(detailEntities);
        PurchaseOrder savedOrder = purchaseOrderRepo.save(order);

        // 4. Cập nhật tồn kho
        for (PurchaseOrderDetail d : savedOrder.getPurchaseOrderDetails()) {
            entityManager.refresh(d); // đảm bảo lấy được ID, cost đã save

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

                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantityToAdd);
                inventory.setLastMovementDate(Instant.now());
                inventoryRepo.save(inventory);
            }
        }

        // 5. Tính tổng tiền đơn hàng
        BigDecimal totalAmount = savedOrder.getPurchaseOrderDetails().stream()
                .map(d -> d.getTotalCost() != null ? d.getTotalCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        savedOrder.setTotalAmount(totalAmount);
        purchaseOrderRepo.save(savedOrder); // update lại totalAmount

        return mapper.map(savedOrder, PurchaseOrderResponse.class);
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
    @Override
    @Transactional
    public PurchaseOrderResponse updatePurchaseOrder(Integer orderId, PurchaseOrderRequest request) {
        // 1. Tìm đơn hàng gốc
        PurchaseOrder order = purchaseOrderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        // 2. Cập nhật thông tin đơn hàng
        order.setStatus(request.getStatus());
        order.setUpdatedAt(Instant.now());

        // 3. Trừ tồn kho từ các chi tiết cũ
        List<PurchaseOrderDetail> oldDetails = detailRepo.findByPurchaseOrderId(orderId);
        for (PurchaseOrderDetail oldDetail : oldDetails) {
            int quantity = (oldDetail.getQuantityReceived() != null && oldDetail.getQuantityReceived() > 0)
                    ? oldDetail.getQuantityReceived() : oldDetail.getQuantityOrdered();

            if (quantity > 0) {
                Inventory inventory = inventoryRepo.findByProduct(oldDetail.getProduct()).orElse(null);
                if (inventory != null) {
                    inventory.setQuantityOnHand(inventory.getQuantityOnHand() - quantity);
                    inventory.setLastMovementDate(Instant.now());
                    inventoryRepo.save(inventory);
                }
            }
        }

        // 4. Xóa chi tiết cũ
        detailRepo.deleteAll(oldDetails);
        order.getPurchaseOrderDetails().clear(); // clear tránh lỗi orphanRemoval

        // 5. Tạo danh sách chi tiết mới
        List<PurchaseOrderDetail> newDetails = new ArrayList<>();
        for (PurchaseOrderDetailRequest detailDto : request.getDetails()) {
            Product product = productRepo.findByBarcode(detailDto.getBarcode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + detailDto.getBarcode()));

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setPurchaseOrder(order);
            detail.setProduct(product);
            detail.setQuantityOrdered(detailDto.getQuantityOrdered());
            detail.setQuantityReceived(detailDto.getQuantityReceived() != null ? detailDto.getQuantityReceived() : 0);
            detail.setUnitCost(detailDto.getUnitCost());
            detail.setTotalCost(detailDto.getTotalCost() != null ? detailDto.getTotalCost() :
                    detail.getUnitCost().multiply(BigDecimal.valueOf(detail.getQuantityOrdered())));
            detail.setCreatedAt(Instant.now());

            newDetails.add(detail);
        }

        // 6. Gán lại danh sách chi tiết vào đơn hàng
        order.getPurchaseOrderDetails().addAll(newDetails);
        PurchaseOrder savedOrder = purchaseOrderRepo.save(order);

        // 7. Cập nhật tồn kho theo chi tiết mới
        for (PurchaseOrderDetail d : savedOrder.getPurchaseOrderDetails()) {
            int quantity = (d.getQuantityReceived() != null && d.getQuantityReceived() > 0)
                    ? d.getQuantityReceived() : d.getQuantityOrdered();

            if (quantity > 0) {
                Inventory inventory = inventoryRepo.findByProduct(d.getProduct())
                        .orElseGet(() -> {
                            Inventory inv = new Inventory();
                            inv.setProduct(d.getProduct());
                            inv.setQuantityOnHand(0);
                            inv.setReservedQuantity(0);
                            inv.setCreatedAt(Instant.now());
                            return inv;
                        });

                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
                inventory.setLastMovementDate(Instant.now());
                inventoryRepo.save(inventory);
            }
        }

        // 8. Cập nhật tổng tiền đơn hàng
        BigDecimal totalAmount = savedOrder.getPurchaseOrderDetails().stream()
                .map(d -> d.getTotalCost() != null ? d.getTotalCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        savedOrder.setTotalAmount(totalAmount);
        purchaseOrderRepo.save(savedOrder);

        // 9. Trả về response
        return mapper.map(savedOrder, PurchaseOrderResponse.class);
    }

    @Override
    @Transactional
    public void deletePurchaseOrderById(Integer orderId) {
        // 1. Lấy đơn hàng
        PurchaseOrder order = purchaseOrderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        // 2. Lấy danh sách chi tiết
        List<PurchaseOrderDetail> details = detailRepo.findByPurchaseOrderId(orderId);

        // 3. Trừ lại tồn kho (nếu đơn hàng đã nhận)
        for (PurchaseOrderDetail detail : details) {
            int quantity = (detail.getQuantityReceived() != null && detail.getQuantityReceived() > 0)
                    ? detail.getQuantityReceived() : detail.getQuantityOrdered();

            if (quantity > 0) {
                Inventory inventory = inventoryRepo.findByProduct(detail.getProduct())
                        .orElse(null);
                if (inventory != null) {
                    inventory.setQuantityOnHand(inventory.getQuantityOnHand() - quantity);
                    inventory.setLastMovementDate(Instant.now());

                    // Đảm bảo tồn kho không âm
                    if (inventory.getQuantityOnHand() < 0) {
                        inventory.setQuantityOnHand(0);
                    }

                    inventoryRepo.save(inventory);
                }
            }
        }

        // 4. Xóa chi tiết đơn hàng
        detailRepo.deleteAll(details);
        order.getPurchaseOrderDetails().clear(); // tránh lỗi orphanRemoval

        // 5. Xóa đơn hàng chính
        purchaseOrderRepo.delete(order);
    }

    @Override
    public boolean isOrderReceived(Integer orderId) {
        PurchaseOrder order = purchaseOrderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        // Đơn hàng có status "received" hoặc bất kỳ logic nào bạn định nghĩa là đã nhập kho
        return "received".equalsIgnoreCase(order.getStatus());
    }

}
