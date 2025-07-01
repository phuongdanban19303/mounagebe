package com.example.repository;

import com.example.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
    List<PurchaseOrder> findAllByOrderByCreatedAtDesc();

    @Query(value = "SELECT supplier_id, SUM(net_amount) AS total_debt FROM purchase_orders WHERE status != 'paid' GROUP BY supplier_id", nativeQuery = true)
    List<Object[]> getSupplierDebtReport();
}