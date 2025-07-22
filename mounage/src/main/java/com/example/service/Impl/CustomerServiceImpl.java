package com.example.service.Impl;

import com.example.entity.Customer;
import com.example.model.Sales.CustomerRequest;
import com.example.model.Sales.CustomerResponse;
import com.example.repository.CustomerRepository;
import com.example.service.Auditable;
import com.example.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CustomerResponse findByPhone(String phone) {
        Customer customer = customerRepo.findByPhone(phone).orElse(null);
        if (customer == null) {
            return null;
        }
        CustomerResponse response = modelMapper.map(customer, CustomerResponse.class);
        return response;

    }

    private final Object lock = new Object();

    private String generateCustomerCode() {
        synchronized (lock) {
            String lastCode = customerRepo.findMaxCustomerCode(); // VD: CT00012
            int nextNumber = 1;
            if (lastCode != null && lastCode.matches("DL\\d{5}")) {
                nextNumber = Integer.parseInt(lastCode.substring(2)) + 1;
            }
            return String.format("DL%05d", nextNumber);
        }
    }



    @Override
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepo.findAll();
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerResponse.class))
                .collect(Collectors.toList());
    }
    @Override
    @Auditable(action = "CREATE", tableName = "customer")
    public CustomerResponse createNewCustomer(CustomerRequest request) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                Customer customer = modelMapper.map(request, Customer.class);
                customer.setCreatedAt(Instant.now());
                customer.setCustomerCode(generateCustomerCode());
                customer = customerRepo.save(customer);
                return modelMapper.map(customer, CustomerResponse.class);
            } catch (DataIntegrityViolationException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    // Retry nếu trùng mã
                    continue;
                } else {
                    throw ex;
                }
            }
        }
        throw new RuntimeException("Không thể tạo customer code duy nhất sau nhiều lần thử.");
    }



    @Override
    @Auditable(action = "DELETE", tableName = "customer")
    public boolean deleteCustomerById(Integer customerId) {
        Optional<Customer> optionalCustomer = customerRepo.findById(customerId);
        if (optionalCustomer.isPresent()) {
            customerRepo.deleteById(customerId);
            return true;
        }
        return false;
    }
    @Override
    @Auditable(action = "UPDATE", tableName = "customer")
    public CustomerResponse updateCustomer(Integer customerId, CustomerRequest request) {
        Optional<Customer> optionalCustomer = customerRepo.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            return null;
        }

        Customer customer = optionalCustomer.get();

        // Cập nhật các trường
        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setGender(request.getGender());
        customer.setCustomerType(request.getCustomerType());
        customer.setUpdatedAt(Instant.now());

        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(request.getDateOfBirth()); // chỉ ngày, không giờ
                customer.setDateOfBirth(dob); // khớp kiểu
            } catch (Exception e) {
                throw new RuntimeException("Invalid dateOfBirth format. Expecting yyyy-MM-dd (e.g., 2000-01-01)");
            }
        }
        // Không cho update customerCode hoặc createdAt

        Customer updated = customerRepo.save(customer);
        return modelMapper.map(updated, CustomerResponse.class);
    }
}
