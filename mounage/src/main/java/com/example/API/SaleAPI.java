package com.example.API;

import com.example.model.Sales.SaleRequest;
import com.example.model.Sales.SaleResponse;
import com.example.model.Sales.SaleDetailResponse;
import com.example.model.Sales.RevenueReportResponse;
import com.example.model.Sales.RevenueReportRequest;
import com.example.model.Sales.ProfitReportRequest;
import com.example.model.Sales.ProfitReportResponse;
import com.example.model.Sales.ProductSalesReportRequest;
import com.example.model.Sales.ProductSalesReportResponse;
import com.example.model.Sales.InventoryReportResponse;
import com.example.model.Sales.SupplierDebtReportResponse;
import com.example.service.SaleService;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin("*")
public class SaleAPI {
    @Autowired
    private SaleService saleService;
    @Autowired
    private UserRepository userRepository;

    // Tạo đơn hàng
    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@RequestBody SaleRequest request, Principal principal) {
        // Lấy cashierId từ user đăng nhập
        Integer cashierId = null;
        if (principal != null) {
            String username = principal.getName();
            var user = userRepository.findByUsername(username);
            if (user != null) {
                cashierId = user.getId();
            }
        }
        SaleResponse response = saleService.createSale(request, cashierId);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách đơn hàng
    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }


    // Báo cáo doanh thu
    @PostMapping("/revenue-report-google")
    public ResponseEntity<List<List<Object>>> getRevenueReportForGoogleChart(@RequestBody RevenueReportRequest request) {
        try {
            Instant start = Instant.parse(request.getStartDate());
            Instant end = Instant.parse(request.getEndDate());
            List<RevenueReportResponse> report = saleService.getRevenueReport(request.getGroupType(), start, end);

            List<List<Object>> data = new ArrayList<>();
            // Header
            data.add(Arrays.asList("Period", "Revenue"));
            // Data rows
            for (RevenueReportResponse r : report) {
                data.add(Arrays.asList(r.getPeriod(), r.getTotalRevenue()));
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Báo cáo lợi nhuận cho Google Charts (POST, trả về mảng 2 chiều, không có màu)
    @PostMapping("/profit-report-google")
    public ResponseEntity<List<List<Object>>> getProfitReportForGoogleChart(@RequestBody ProfitReportRequest request) {
        try {
            Instant start = Instant.parse(request.getStartDate());
            Instant end = Instant.parse(request.getEndDate());
            List<ProfitReportResponse> report = saleService.getProfitReport(request.getGroupType(), start, end);

            List<List<Object>> data = new ArrayList<>();
            // Header
            data.add(Arrays.asList("Period", "Profit"));
            // Data rows
            for (ProfitReportResponse r : report) {
                data.add(Arrays.asList(r.getPeriod(), r.getTotalProfit()));
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{saleId}/detail")
    public ResponseEntity<List<SaleDetailResponse>> getSaleDetail(@PathVariable Integer saleId) {
        return ResponseEntity.ok(saleService.getSaleDetail(saleId));
    }

    // Báo cáo sản phẩm bán chạy/bán chậm cho Google Charts (POST, trả về mảng 2 chiều)
    @PostMapping("/product-sales-report-google")
    public ResponseEntity<List<List<Object>>> getProductSalesReportForGoogleChart(@RequestBody ProductSalesReportRequest request) {
        try {
            Instant start = Instant.parse(request.getStartDate());
            Instant end = Instant.parse(request.getEndDate());
            List<ProductSalesReportResponse> report = saleService.getProductSalesReport(request.getSort(), start, end);

            List<List<Object>> data = new ArrayList<>();
            // Header
            data.add(Arrays.asList("Product", "TotalQuantity"));
            // Data rows
            for (ProductSalesReportResponse r : report) {
                data.add(Arrays.asList(r.getProductName(), r.getTotalQuantity()));
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Báo cáo tồn kho cho Google Charts (GET, trả về mảng 2 chiều)
    @GetMapping("/inventory-report")
    public ResponseEntity<List<List<Object>>> getInventoryReportForGoogleChart() {
        try {
            List<InventoryReportResponse> report = saleService.getInventoryReport();
            List<List<Object>> data = new ArrayList<>();
            // Header
            data.add(Arrays.asList("Product", "QuantityOnHand", "CostPrice", "InventoryValue", "LastMovementDate"));
            // Data rows
            for (InventoryReportResponse r : report) {
                data.add(Arrays.asList(r.getProductName(), r.getQuantityOnHand(), r.getCostPrice(), r.getInventoryValue(), r.getLastMovementDate()));
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Báo cáo công nợ nhà cung cấp cho Google Charts (GET, trả về mảng 2 chiều)
    @GetMapping("/supplier-debt-report")
    public ResponseEntity<List<List<Object>>> getSupplierDebtReportForGoogleChart() {
            try {
                List<SupplierDebtReportResponse> report = saleService.getSupplierDebtReport();
                List<List<Object>> data = new ArrayList<>();
                // Header
                data.add(Arrays.asList("Supplier", "TotalDebt"));
                // Data rows
                for (SupplierDebtReportResponse r : report) {
                    data.add(Arrays.asList(r.getSupplierName(), r.getTotalDebt()));
                }
                return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteSale(@PathVariable Integer id) {
        saleService.deleteSaleById(id);
        return ResponseEntity.ok("Đã xóa đơn hàng thành công");
    }
    @GetMapping("/by-sale-number")
    public ResponseEntity<SaleResponse> getSaleBySaleNumber(@RequestParam String saleNumber) {
        return saleService.findSaleBySaleNumber(saleNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}