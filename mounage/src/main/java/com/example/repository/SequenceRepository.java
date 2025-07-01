package com.example.repository;

import com.example.entity.SequenceNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SequenceRepository extends JpaRepository<SequenceNumber, String> {
    Optional<SequenceNumber> findBySequenceName(String sequenceName);
} 