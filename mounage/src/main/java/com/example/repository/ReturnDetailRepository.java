package com.example.repository;

import com.example.entity.ReturnDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReturnDetailRepository extends JpaRepository<ReturnDetail, Integer> {
    @Query("SELECT COALESCE(SUM(r.quantityReturned), 0) FROM ReturnDetail r WHERE r.originalDetailId = :detailId")
    Optional<Integer> sumQuantityReturnedByOriginalDetailId(@Param("detailId") Integer detailId);
}
