package com.example.service.Impl;

import com.example.entity.Customer;
import com.example.model.Sales.CustomerRequest;
import com.example.model.Sales.CustomerResponse;
import com.example.repository.CustomerRepository;
import com.example.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CustomerResponse findByPhone(String phone) {
        Customer customer = customerRepo.findByPhone(phone).orElse(null);
        if (customer == null) {
            return null;
        }
        CustomerResponse response = modelMapper.map(customer, CustomerResponse.class);
        return response;

    }
        private String generateCustomerCode() {
        long count = customerRepo.count() + 1;
        return String.format("KH%05d", count); // ví dụ KH00001
    }

    @Override
    public CustomerResponse createNewCustomer(CustomerRequest request) {
        Customer customer = modelMapper.map(request, Customer.class);
        customer.setCreatedAt(Instant.now());
        customer.setCustomerCode(generateCustomerCode()); // <-- thêm dòng này
        customer = customerRepo.save(customer);
        CustomerResponse response = modelMapper.map(customer, CustomerResponse.class);
        return response;

    }
}
