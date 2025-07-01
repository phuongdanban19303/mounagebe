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
import com.example.repository.CustomerRepository;
import com.example.repository.InventoryRepository;
import com.example.repository.ProductRepository;
import com.example.repository.SaleDetailRepository;
import com.example.repository.SaleRepository;
import com.example.repository.UserRepository;
import com.example.service.SaleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleServiceImpl implements SaleService {
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

    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest request, Integer cashierId) {
        // 0. CHECK INVENTORY
        for (SaleItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId().longValue())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại với ID: " + item.getProductId()));

            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tồn kho cho sản phẩm: " + product.getProductName()));

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: '" + product.getProductName() + "'. Chỉ còn " + inventory.getAvailableQuantity() + " sản phẩm.");
            }
        }
        // 1. Xử lý khách hàng
        Customer customer = null;
        if (request.getCustomer().getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomer().getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        }

        // 2. Tính toán tổng tiền
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        for (SaleItemRequest item : request.getItems()) {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
            if (item.getDiscountPercent() != null && item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                discountAmount = discountAmount.add(
                        lineTotal.multiply(item.getDiscountPercent()).divide(BigDecimal.valueOf(100))
                );
            }
        }
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(taxAmount);

        // 3. Tạo đơn hàng (Sale) và lưu
        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setSaleDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        sale.setCashierId(cashierId);
        sale.setSubtotal(subtotal);
        sale.setDiscountAmount(discountAmount);
        sale.setTaxAmount(taxAmount);
        sale.setTotalAmount(totalAmount);
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setCashReceived(request.getCashReceived());
        sale.setChangeGiven(request.getCashReceived().subtract(totalAmount));
        sale.setStatus("completed");
        sale.setNotes(request.getNotes());
        sale.setSaleNumber(generateSaleNumber());

        sale = saleRepository.save(sale); // <- phải có dòng này để tạo biến 'sale'

        // 4. Tạo danh sách SaleDetail
        for (SaleItemRequest item : request.getItems()) {
            // CẬP NHẬT TỒN KHO
            Product product = productRepository.findById(item.getProductId().longValue()).get();
            Inventory inventory = inventoryRepository.findByProduct(product).get();
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() - item.getQuantity());
            inventory.setLastMovementDate(Instant.now());
            inventoryRepository.save(inventory);

            SaleDetail detail = new SaleDetail();
            detail.setSale(sale); // giờ không còn lỗi 'cannot resolve'
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setDiscountPercent(item.getDiscountPercent());
            detail.setCostPrice(item.getCostPrice());

            // Tính discountAmount
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal discount = BigDecimal.ZERO;
            if (item.getDiscountPercent() != null && item.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                discount = lineTotal.multiply(item.getDiscountPercent()).divide(BigDecimal.valueOf(100));
            }
            detail.setDiscountAmount(discount);

            SaleDetail saved = saleDetailRepository.save(detail);
            entityManager.refresh(saved); // cập nhật các trường generated như createdAt, lineTotal
        }

        // 5. Trả về response
        SaleResponse response = new SaleResponse();
        response.setSaleId(sale.getId());
        response.setSaleNumber(sale.getSaleNumber());
        response.setTotalAmount(sale.getTotalAmount());
        response.setChangeGiven(sale.getChangeGiven());
        response.setStatus(sale.getStatus());
        return response;
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
}