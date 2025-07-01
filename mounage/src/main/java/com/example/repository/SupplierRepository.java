package com.example.repository;

import com.example.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsBySupplierCode(String supplierCode);
}
