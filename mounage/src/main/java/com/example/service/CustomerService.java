package com.example.service;

import com.example.entity.Customer;
import com.example.model.Sales.CustomerRequest;
import com.example.model.Sales.CustomerResponse;

public interface CustomerService {
    CustomerResponse findByPhone(String phone);
    CustomerResponse createNewCustomer(CustomerRequest request);
}
