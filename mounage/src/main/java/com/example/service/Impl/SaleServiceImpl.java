package com.example.service.Impl;

import com.example.entity.Customer;
import com.example.entity.Inventory;
import com.example.entity.Product;
import com.example.entity.Sale;
import com.example.entity.SaleDetail;
import com.example.model.Sales.SaleItemRequest;
import com.example.model.Sales.SaleRequest;
import com.example.model.Sales.SaleResponse;
import com.example.model.Sales.SaleDetailResponse;
import com.example.model.Sales.RevenueReportResponse;
import com.example.model.Sales.ProfitReportResponse;
import com.example.model.Sales.ProductSalesReportResponse;
import com.example.model.Sales.InventoryReportResponse;
import com.example.model.Sales.SupplierDebtReportResponse;
import com.example.repository.CustomerRepository;
import com.example.repository.InventoryRepository;
import com.example.repository.ProductRepository;
import com.example.repository.SaleDetailRepository;
import com.example.repository.SaleRepository;
import com.example.repository.UserRepository;
import com.example.repository.SupplierRepository;
import com.example.repository.PurchaseOrderRepository;
import com.example.service.SaleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class    SaleServiceImpl implements SaleService {
    @Autowired
    private SaleRepository saleRepository;
    @Autowired private SaleDetailRepository saleDetailRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest request, Integer cashierId) {
        // 0. Kiểm tra tồn kho
        for (SaleItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId().longValue())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại với ID: " + item.getProductId()));

            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho cho sản phẩm: " + product.getProductName()));

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new RuntimeException("Không đủ tồn kho cho sản phẩm: " + product.getProductName());
            }
        }

        // 1. Xử lý khách hàng
        Customer customer = null;
        if (request.getCustomer() != null && request.getCustomer().getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomer().getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        }

        // 2. Map đơn hàng từ request
        Sale sale = modelMapper.map(request, Sale.class);
        sale.setCustomer(customer);
        sale.setCashierId(cashierId);
        sale.setSaleDate(Instant.now());
        sale.setSaleNumber(generateSaleNumber());
        sale.setStatus("completed");

        // 3. Tính toán giá trị đơn hàng
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO; // nếu có

        Set<SaleDetail> saleDetails = new HashSet<>();
        for (SaleItemRequest item : request.getItems()) {
            // Tính lineTotal
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal discount = BigDecimal.ZERO;
            if (item.getDiscountPercent() != null && item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                discount = lineTotal.multiply(item.getDiscountPercent()).divide(BigDecimal.valueOf(100));
            }

            subtotal = subtotal.add(lineTotal);
            discountAmount = discountAmount.add(discount);

            // Cập nhật tồn kho
            Product product = productRepository.findById(item.getProductId().longValue()).get();
            Inventory inventory = inventoryRepository.findByProduct(product).get();
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() - item.getQuantity());
            inventory.setLastMovementDate(Instant.now());
            inventoryRepository.save(inventory);

            // Tạo SaleDetail
            SaleDetail detail = modelMapper.map(item, SaleDetail.class);
            detail.setId(null); // BẮT BUỘC, nếu không Hibernate sẽ hiểu là UPDA
            detail.setSale(sale); // bắt buộc để map quan hệ
            detail.setProductId(item.getProductId());
            detail.setCostPrice(item.getCostPrice());
            detail.setDiscountAmount(discount);
            detail.setLineTotal(lineTotal.subtract(discount));

            saleDetails.add(detail);
        }

        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(taxAmount);
        BigDecimal changeGiven = request.getCashReceived().subtract(totalAmount);

        // Gán vào Sale
        sale.setSubtotal(subtotal);
        sale.setDiscountAmount(discountAmount);
        sale.setTaxAmount(taxAmount);
        sale.setTotalAmount(totalAmount);
        sale.setChangeGiven(changeGiven);

        // 4. Lưu đơn hàng
        Sale savedSale = saleRepository.save(sale);

        // 5. Lưu chi tiết đơn hàng (nên lưu sau khi đã có ID của sale)
        for (SaleDetail detail : saleDetails) {
            detail.setSale(savedSale);
            saleDetailRepository.save(detail);
        }

        // 6. Trả kết quả
        return modelMapper.map(savedSale, SaleResponse.class);
    }



    // Hàm sinh mã hóa đơn tự động (ví dụ: HD0001)
    private String generateSaleNumber() {
        long count = saleRepository.count() + 1;
        return String.format("HD%05d", count);
    }

    @Override
    public List<SaleResponse> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream().map(sale -> {
            SaleResponse response = new SaleResponse();
            response.setSaleId(sale.getId());
            response.setSaleNumber(sale.getSaleNumber());
            response.setCustomerName(sale.getCustomer() != null ? sale.getCustomer().getFullName() : null);
            response.setSaleDate(sale.getSaleDate());
            // Lấy tên nhân viên theo cashierId
            response.setCashierName(sale.getCashierId() != null ?
                    userRepository.findById(String.valueOf(sale.getCashierId())).map(u -> u.getFullName()).orElse("") : null);
            response.setSubtotal(sale.getSubtotal());
            response.setTotalAmount(sale.getTotalAmount());
            response.setPaymentMethod(sale.getPaymentMethod());
            response.setCashReceived(sale.getCashReceived());
            response.setStatus(sale.getStatus());
            response.setNotes(sale.getNotes());
            response.setCreatedAt(sale.getCreatedAt());
            response.setDiscountAmount(sale.getDiscountAmount());
            response.setChangeGiven(sale.getChangeGiven());
            response.setSaleDetail(
                    sale.getSaleDetails() != null ?
                            sale.getSaleDetails().stream()
                                    .map(detail -> modelMapper.map(detail, SaleDetailResponse.class))
                                    .collect(Collectors.toList())
                            : new ArrayList<>()
            );
            return response;
        }).toList();
    }

    @Override
    public List<SaleDetailResponse> getSaleDetail(Integer saleId) {
        // Lấy danh sách SaleDetail từ DB theo saleId
        List<SaleDetail> details = saleDetailRepository.findBySaleId(saleId);

        return details.stream().map(detail -> {
            SaleDetailResponse d = new SaleDetailResponse();
            d.setProductId(detail.getProductId());

            // Lấy tên sản phẩm từ productId
            d.setProductName(detail.getProductId() != null
                    ? productRepository.findById(detail.getProductId().longValue())
                    .map(p -> p.getProductName())
                    .orElse("")
                    : null);

            d.setQuantity(detail.getQuantity());
            d.setUnitPrice(detail.getUnitPrice());
            d.setDiscountPercent(detail.getDiscountPercent());
            d.setDiscountAmount(detail.getDiscountAmount()); // lấy từ DB
            d.setLineTotal(detail.getLineTotal());           // lấy từ DB
            d.setCreatedAt(detail.getCreatedAt());           // lấy từ DB

            return d;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RevenueReportResponse> getRevenueReport(String groupType, Instant startDate, Instant endDate) {
        List<Object[]> results;
        switch (groupType.toLowerCase()) {
            case "day" -> results = saleRepository.getRevenueByDay(startDate, endDate);
            case "week" -> results = saleRepository.getRevenueByWeek(startDate, endDate);
            case "month" -> results = saleRepository.getRevenueByMonth(startDate, endDate);
            case "quarter" -> results = saleRepository.getRevenueByQuarter(startDate, endDate);
            case "year" -> results = saleRepository.getRevenueByYear(startDate, endDate);
            default -> throw new IllegalArgumentException("Invalid groupType: " + groupType);
        }
        return results.stream()
                .map(row -> new RevenueReportResponse(String.valueOf(row[0]), (java.math.BigDecimal) row[1]))
                .toList();
    }

    @Override
    public List<ProfitReportResponse> getProfitReport(String groupType, Instant startDate, Instant endDate) {
        List<Object[]> results;
        switch (groupType.toLowerCase()) {
            case "day" -> results = saleDetailRepository.getProfitByDay(startDate, endDate);
            case "week" -> results = saleDetailRepository.getProfitByWeek(startDate, endDate);
            case "month" -> results = saleDetailRepository.getProfitByMonth(startDate, endDate);
            case "quarter" -> results = saleDetailRepository.getProfitByQuarter(startDate, endDate);
            case "year" -> results = saleDetailRepository.getProfitByYear(startDate, endDate);
            default -> throw new IllegalArgumentException("Invalid groupType: " + groupType);
        }
        return results.stream()
                .map(row -> new ProfitReportResponse(String.valueOf(row[0]), (java.math.BigDecimal) row[1]))
                .toList();
    }

    @Override
    public List<ProductSalesReportResponse> getProductSalesReport(String sort, Instant startDate, Instant endDate) {
        List<Object[]> results;
        if ("asc".equalsIgnoreCase(sort)) {
            results = saleDetailRepository.getProductSalesAsc(startDate, endDate);
        } else {
            results = saleDetailRepository.getProductSalesDesc(startDate, endDate);
        }
        return results.stream().map(row -> {
            Integer productId = ((Number) row[0]).intValue();
            Integer totalQuantity = ((Number) row[1]).intValue();
            String productName = productRepository.findById(productId.longValue())
                    .map(p -> p.getProductName())
                    .orElse("");
            return new ProductSalesReportResponse(productId, productName, totalQuantity);
        }).toList();
    }

    @Override
    public List<InventoryReportResponse> getInventoryReport() {
        var inventories = inventoryRepository.findAll();
        return inventories.stream().map(inv -> {
            var product = inv.getProduct();
            Integer productId = product.getId();
            String productName = product.getProductName();
            Integer quantityOnHand = inv.getQuantityOnHand();
            java.math.BigDecimal costPrice = product.getCostPrice();
            java.math.BigDecimal inventoryValue = costPrice.multiply(java.math.BigDecimal.valueOf(quantityOnHand));
            java.time.Instant lastMovementDate = inv.getLastMovementDate();
            return new InventoryReportResponse(productId, productName, quantityOnHand, costPrice, inventoryValue, lastMovementDate);
        }).toList();
    }

    @Override
    public List<SupplierDebtReportResponse> getSupplierDebtReport() {
        List<Object[]> results = purchaseOrderRepository.getSupplierDebtReport();
        return results.stream().map(row -> {
            Integer supplierId = ((Number) row[0]).intValue();
            java.math.BigDecimal totalDebt = (java.math.BigDecimal) row[1];
            String supplierName = supplierRepository.findById(supplierId.longValue())
                    .map(s -> s.getSupplierName())
                    .orElse("");
            return new SupplierDebtReportResponse(supplierId, supplierName, totalDebt);
        }).toList();
    }@Override
    public Optional<SaleResponse> findSaleBySaleNumber(String saleNumber) {
        return saleRepository.findBySaleNumber(saleNumber)
                .map(sale -> {
                    SaleResponse response = modelMapper.map(sale, SaleResponse.class);

                    // Khách hàng
                    response.setCustomerName(sale.getCustomer() != null ? sale.getCustomer().getFullName() : null);

                    // Thu ngân
                    response.setCashierName(sale.getCashierId() != null ?
                            userRepository.findById(String.valueOf(sale.getCashierId()))
                                    .map(u -> u.getFullName()).orElse("") : null);

                    // SaleDetail
                    List<SaleDetail> details = sale.getSaleDetails() != null
                            ? new ArrayList<>(sale.getSaleDetails()) : new ArrayList<>();

                    Set<Long> productIds = details.stream()
                            .map(SaleDetail::getProductId)
                            .filter(Objects::nonNull)
                            .map(Long::valueOf)
                            .collect(Collectors.toSet());

                    // Load product name + barcode
                    Map<Integer, Product> productMap = productRepository.findAllById(productIds).stream()
                            .collect(Collectors.toMap(Product::getId, p -> p));

                    List<SaleDetailResponse> detailResponses = details.stream().map(detail -> {
                        SaleDetailResponse d = new SaleDetailResponse();
                        d.setSaledetailId(detail.getId());
                        d.setProductId(detail.getProductId());

                        Product product = productMap.get(detail.getProductId());
                        d.setProductName(product != null ? product.getProductName() : "");
                        d.setBardcode(product != null ? product.getBarcode() : ""); // ✅ THÊM Ở ĐÂY

                        d.setQuantity(detail.getQuantity());
                        d.setUnitPrice(detail.getUnitPrice());
                        d.setDiscountPercent(detail.getDiscountPercent());
                        d.setDiscountAmount(detail.getDiscountAmount());
                        d.setLineTotal(detail.getLineTotal());
                        d.setCreatedAt(detail.getCreatedAt());
                        return d;
                    }).collect(Collectors.toList());

                    response.setSaleDetail(detailResponses);
                    return response;
                });
    }

    @Override
    @Transactional
    public void deleteSaleById(Integer saleId) {
        // 1. Tìm đơn hàng
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + saleId));

        // 2. Lấy và xóa chi tiết đơn hàng
        List<SaleDetail> saleDetails = saleDetailRepository.findBySaleId(saleId);
        saleDetailRepository.deleteAll(saleDetails);

        // 3. Xóa đơn hàng
        saleRepository.delete(sale);
    }


}

