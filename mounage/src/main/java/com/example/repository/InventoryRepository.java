package com.example.repository;

import com.example.entity.Inventory;
import com.example.entity.Product;
import com.example.model.Invertory.ExpiringProductResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProduct(Product product);
    Optional<Inventory> findByProductId(int productId);

    @Query("SELECT i FROM Inventory i WHERE i.quantityOnHand <= i.product.reorderPoint")
    List<Inventory> findLowStockProducts();


}