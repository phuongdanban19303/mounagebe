package com.example.API;

import com.example.model.Sales.SaleRequest;
import com.example.model.Sales.SaleResponse;
import com.example.model.Sales.SaleDetailResponse;
import com.example.service.SaleService;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
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

    // Lấy chi tiết đơn hàng
    @GetMapping("/{saleId}")
    public ResponseEntity<List<SaleDetailResponse>> getSaleDetail(@PathVariable Integer saleId) {
        return ResponseEntity.ok(saleService.getSaleDetail(saleId));
    }
}