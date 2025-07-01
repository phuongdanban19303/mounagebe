package com.example.service;

import com.example.entity.Customer;
import com.example.model.Sales.CustomerRequest;
import com.example.model.Sales.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse findByPhone(String phone);
    CustomerResponse createNewCustomer(CustomerRequest request);
    List<CustomerResponse> getAllCustomers();
    boolean deleteCustomerById(Integer customerId);
    CustomerResponse updateCustomer(Integer customerId, CustomerRequest request);
}
