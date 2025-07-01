package com.example.API;

import com.example.model.Sales.CustomerRequest;
import com.example.model.Sales.CustomerResponse;
import com.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin("*")
public class CostumerAPI {
@Autowired
    private CustomerService customerService;
    @GetMapping("/by-phone")
    public ResponseEntity<?> getCustomerByPhone(@RequestParam String phone) {
        CustomerResponse customer = customerService.findByPhone(phone);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khách hàng");
        }
        return ResponseEntity.ok( customer);
    }
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createNewCustomer(request)
);
    }
}
