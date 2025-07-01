package com.example.repository;

import com.example.entity.PackagingType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackagingRepository extends JpaRepository<PackagingType, Long> {}

