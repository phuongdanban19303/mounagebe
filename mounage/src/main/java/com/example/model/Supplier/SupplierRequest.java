package com.example.model.Supplier;

import lombok.Data;

@Data
public class SupplierRequest {
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private String paymentTerms;
    private Boolean isActive;
}
