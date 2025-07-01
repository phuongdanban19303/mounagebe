package com.example.repository;

import com.example.entity.PurchaseOrderDetail;
import com.example.model.Invertory.ExpiringProductResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Integer> {
    List<PurchaseOrderDetail> findByPurchaseOrderId(Integer purchaseOrderId);
    @Query("SELECT new com.example.model.Invertory.ExpiringProductResponse(p.product.id, p.product.productName, " +
            "p.batchNumber, p.expiryDate, p.quantityReceived) " +
            "FROM PurchaseOrderDetail p WHERE p.expiryDate IS NOT NULL AND p.expiryDate <= :targetDate")
    List<ExpiringProductResponse> findExpiringProducts(@Param("targetDate") LocalDate targetDate);
}
