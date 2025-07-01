package com.example.repository;

import com.example.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByPhone(String phone);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.lastPurchaseDate >= :start AND c.lastPurchaseDate < :end")
    Long countByCreatedAt(@Param("start") LocalDate start, @Param("end") LocalDate end);
    @Query("SELECT MAX(c.customerCode) FROM Customer c")
    String findMaxCustomerCode();

}
