package com.example.repository;

import com.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
    SELECT p FROM Product p
    WHERE (:name IS NULL OR p.productName LIKE %:name%)
      AND (:barcode IS NULL OR p.barcode LIKE %:barcode%)
      AND (:categoryId IS NULL OR p.category.id = :categoryId)
      AND (:supplierId IS NULL OR p.supplier.id = :supplierId)
""")
    List<Product> search(@Param("name") String name,
                         @Param("barcode") String barcode,
                         @Param("categoryId") Long categoryId,
                         @Param("supplierId") Long supplierId);

    @Query("""
    SELECT p FROM Product p
    WHERE (:key IS NULL OR p.productName LIKE %:key% OR p.productCode LIKE %:key% OR p.barcode LIKE %:key%)
    """)
    List<Product> autocompleteSearch(@Param("key") String key);

    Optional<Product> findByBarcode(String barcode);
}
