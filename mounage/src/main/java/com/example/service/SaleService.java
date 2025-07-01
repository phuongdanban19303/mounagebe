package com.example.service;

import com.example.model.Sales.SaleRequest;
import com.example.model.Sales.SaleResponse;
import com.example.model.Sales.SaleDetailResponse;
import com.example.model.Sales.RevenueReportResponse;
import com.example.model.Sales.ProfitReportResponse;
import com.example.model.Sales.ProductSalesReportResponse;
import com.example.model.Sales.InventoryReportResponse;
import com.example.model.Sales.SupplierDebtReportResponse;

import java.util.List;
import java.time.Instant;
import java.util.Optional;

public interface SaleService {
    SaleResponse createSale(SaleRequest request, Integer cashierId);
    List<SaleResponse> getAllSales();
    List<SaleDetailResponse> getSaleDetail(Integer saleId);
    List<RevenueReportResponse> getRevenueReport(String groupType, Instant startDate, Instant endDate);
    List<ProfitReportResponse> getProfitReport(String groupType, Instant startDate, Instant endDate);
    List<ProductSalesReportResponse> getProductSalesReport(String sort, Instant startDate, Instant endDate);
    List<InventoryReportResponse> getInventoryReport();
    List<SupplierDebtReportResponse> getSupplierDebtReport();
    void deleteSaleById(Integer saleId);
    Optional<SaleResponse> findSaleBySaleNumber(String saleNumber);

}