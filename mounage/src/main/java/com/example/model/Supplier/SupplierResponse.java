package com.example.model.Supplier;

import lombok.Data;

import java.time.Instant;

@Data
public class SupplierResponse {
    private Integer id;
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private String paymentTerms;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
