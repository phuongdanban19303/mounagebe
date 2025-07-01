package com.example.model.Sales;

public class ProductSalesReportRequest {
    private String startDate;
    private String endDate;
    private String sort; // "asc" hoặc "desc"

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
} 