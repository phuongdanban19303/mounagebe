package com.example.repository;

import com.example.entity.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Integer> {
    List<SaleDetail> findBySaleId(Integer saleId);
}

