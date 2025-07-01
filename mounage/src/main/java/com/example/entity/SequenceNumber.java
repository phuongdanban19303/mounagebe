package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sequence_numbers")
@Data
@NoArgsConstructor
public class SequenceNumber {

    @Id
    @Column(name = "sequence_name", nullable = false)
    private String sequenceName;

    @Column(name = "current_value", nullable = false)
    private Long currentValue;

    public SequenceNumber(String sequenceName, Long currentValue) {
        this.sequenceName = sequenceName;
        this.currentValue = currentValue;
    }
}