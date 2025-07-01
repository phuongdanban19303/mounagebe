package com.example.model.Sales;

import java.math.BigDecimal;

public class SupplierDebtReportResponse {
    private Integer supplierId;
    private String supplierName;
    private BigDecimal totalDebt;

    public SupplierDebtReportResponse(Integer supplierId, String supplierName, BigDecimal totalDebt) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.totalDebt = totalDebt;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(BigDecimal totalDebt) {
        this.totalDebt = totalDebt;
    }
} 