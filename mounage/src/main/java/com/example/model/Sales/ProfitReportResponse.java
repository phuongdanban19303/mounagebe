package com.example.model.Sales;

import java.math.BigDecimal;

public class ProfitReportResponse {
    private String period;
    private BigDecimal totalProfit;

    public ProfitReportResponse(String period, BigDecimal totalProfit) {
        this.period = period;
        this.totalProfit = totalProfit;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }
} 