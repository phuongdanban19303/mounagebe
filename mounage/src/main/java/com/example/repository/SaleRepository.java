package com.example.repository;

import com.example.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
    @Query(value = "SELECT DATE_FORMAT(sale_date, '%Y-%m-%d') AS period, SUM(total_amount) AS total_revenue FROM sales WHERE sale_date BETWEEN :startDate AND :endDate GROUP BY period ORDER BY period", nativeQuery = true)
    List<Object[]> getRevenueByDay(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query(value = "SELECT CONCAT(YEAR(sale_date), '-W', LPAD(WEEK(sale_date, 1), 2, '0')) AS period, SUM(total_amount) AS total_revenue FROM sales WHERE sale_date BETWEEN :startDate AND :endDate GROUP BY period ORDER BY period", nativeQuery = true)
    List<Object[]> getRevenueByWeek(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query(value = "SELECT DATE_FORMAT(sale_date, '%Y-%m') AS period, SUM(total_amount) AS total_revenue FROM sales WHERE sale_date BETWEEN :startDate AND :endDate GROUP BY period ORDER BY period", nativeQuery = true)
    List<Object[]> getRevenueByMonth(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query(value = "SELECT CONCAT(YEAR(sale_date), '-Q', QUARTER(sale_date)) AS period, SUM(total_amount) AS total_revenue FROM sales WHERE sale_date BETWEEN :startDate AND :endDate GROUP BY period ORDER BY period", nativeQuery = true)
    List<Object[]> getRevenueByQuarter(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query(value = "SELECT YEAR(sale_date) AS period, SUM(total_amount) AS total_revenue FROM sales WHERE sale_date BETWEEN :startDate AND :endDate GROUP BY period ORDER BY period", nativeQuery = true)
    List<Object[]> getRevenueByYear(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    Optional<Sale> findBySaleNumber(String saleNumber);

}
