package com.example.service;

import com.example.model.Sales.SaleRequest;
import com.example.model.Sales.SaleResponse;
import com.example.model.Sales.SaleDetailResponse;

import java.util.List;

public interface SaleService {
    SaleResponse createSale(SaleRequest request, Integer cashierId);
    List<SaleResponse> getAllSales();
    List<SaleDetailResponse> getSaleDetail(Integer saleId);
}