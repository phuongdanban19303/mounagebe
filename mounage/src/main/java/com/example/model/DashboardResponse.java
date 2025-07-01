package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal revenueToday;
    private Long newOrders;
    private Long lowStockProducts;
    private Long newCustomers;
}
