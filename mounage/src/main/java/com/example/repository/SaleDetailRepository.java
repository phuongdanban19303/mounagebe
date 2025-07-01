package com.example.repository;

import com.example.entity.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Integer> {
    List<SaleDetail> findBySaleId(Integer saleId);

    @Query("SELECT FUNCTION('DATE_FORMAT', s.sale.saleDate, '%Y-%m-%d'), " +
            "SUM((s.unitPrice - s.costPrice) * s.quantity) " +
            "FROM SaleDetail s " +
            "WHERE s.sale.saleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', s.sale.saleDate, '%Y-%m-%d') " +
            "ORDER BY FUNCTION('DATE_FORMAT', s.sale.saleDate, '%Y-%m-%d')")
    List<Object[]> getProfitByDay(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT FUNCTION('YEARWEEK', s.sale.saleDate), " +
            "SUM((s.unitPrice - s.costPrice) * s.quantity) " +
            "FROM SaleDetail s " +
            "WHERE s.sale.saleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('YEARWEEK', s.sale.saleDate) " +
            "ORDER BY FUNCTION('YEARWEEK', s.sale.saleDate)")
    List<Object[]> getProfitByWeek(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', s.sale.saleDate, '%Y-%m'), " +
            "SUM((s.unitPrice - s.costPrice) * s.quantity) " +
            "FROM SaleDetail s " +
            "WHERE s.sale.saleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', s.sale.saleDate, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', s.sale.saleDate, '%Y-%m')")
    List<Object[]> getProfitByMonth(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT CONCAT(YEAR(s.sale.saleDate), '-Q', FUNCTION('QUARTER', s.sale.saleDate)), " +
            "SUM((s.unitPrice - s.costPrice) * s.quantity) " +
            "FROM SaleDetail s " +
            "WHERE s.sale.saleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY CONCAT(YEAR(s.sale.saleDate), '-Q', FUNCTION('QUARTER', s.sale.saleDate)) " +
            "ORDER BY CONCAT(YEAR(s.sale.saleDate), '-Q', FUNCTION('QUARTER', s.sale.saleDate))")
    List<Object[]> getProfitByQuarter(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT FUNCTION('YEAR', s.sale.saleDate), " +
            "SUM((s.unitPrice - s.costPrice) * s.quantity) " +
            "FROM SaleDetail s " +
            "WHERE s.sale.saleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('YEAR', s.sale.saleDate) " +
            "ORDER BY FUNCTION('YEAR', s.sale.saleDate)")
    List<Object[]> getProfitByYear(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT s.productId, SUM(s.quantity) " +
            "FROM SaleDetail s " +
            "WHERE s.sale.saleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.productId " +
            "ORDER BY SUM(s.quantity) ASC")
    List<Object[]> getProductSalesAsc(@Param("startDate") Instant startDate,
                                      @Param("endDate") Instant endDate);

    @Query("SELECT s.productId, SUM(s.quantity) " +
            "FROM SaleDetail s " +
            "WHERE s.sale.saleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.productId " +
            "ORDER BY SUM(s.quantity) DESC")
    List<Object[]> getProductSalesDesc(@Param("startDate") Instant startDate,
                                       @Param("endDate") Instant endDate);
}
