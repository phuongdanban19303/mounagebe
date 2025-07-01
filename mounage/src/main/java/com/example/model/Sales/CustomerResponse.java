package com.example.model.Sales;

import lombok.Data;

@Data
public class CustomerResponse {
    private Integer id;
    private String fullName;
    private String phone;
    private String address;
    private String dateOfBirth; // optional
    private String gender;      // optional
    private String customerType;
    private String createdAt; // optiona
}
