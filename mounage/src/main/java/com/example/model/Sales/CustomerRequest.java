package com.example.model.Sales;

import lombok.Data;

@Data
public class CustomerRequest {
    private Integer customerId;
    private String customerCode; // optional
    private String fullName;
    private String phone;
    private String address;
    private String dateOfBirth; // optional
    private String gender;      // optional
    private String customerType;// optional
    // getter/setter
}