package com.example.API;

import com.example.model.Sales.CustomerRequest;
import com.example.model.Sales.CustomerResponse;
import com.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable("id") Integer customerId,
            @RequestBody CustomerRequest request
    ) {
        CustomerResponse updated = customerService.updateCustomer(customerId, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // 5. Xoá khách hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") Integer customerId) {
        boolean deleted = customerService.deleteCustomerById(customerId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Customer deleted successfully.");
    }
}
