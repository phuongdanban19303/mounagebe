package com.example.model.Sales;

import java.math.BigDecimal;
import java.time.Instant;

public class RevenueReportResponse {
    private String period; // Ví dụ: "2024-06-01" hoặc "2024-W23" hoặc "2024-Q2" hoặc "2024" tuỳ kiểu group
    private BigDecimal totalRevenue;

    public RevenueReportResponse(String period, BigDecimal totalRevenue) {
        this.period = period;
        this.totalRevenue = totalRevenue;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
} 