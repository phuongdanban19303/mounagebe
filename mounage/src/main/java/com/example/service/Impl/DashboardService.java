package com.example.service.Impl;

import com.example.model.DashboardResponse;
import com.example.repository.CustomerRepository;
import com.example.repository.ProductRepository;
import com.example.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public DashboardResponse getDashboardMetrics() {
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        BigDecimal revenue = saleRepository.getRevenueByDate(startOfDay, endOfDay);
        Long orderCount = saleRepository.countBySaleDate(startOfDay, endOfDay);
        Long lowStock = productRepository.countLowStock();

        // ❗ Sửa ở đây: dùng LocalDate cho customer query
        Long newCustomers = customerRepository.countByCreatedAt(today, today.plusDays(1));

        return new DashboardResponse(revenue, orderCount, lowStock, newCustomers);
    }
}
