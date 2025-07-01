package com.example.API;

import com.example.model.Supplier.SupplierRequest;
import com.example.model.Supplier.SupplierResponse;
import com.example.service.Impl.SupplierServiceImpl;
import com.example.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierAPI{

    @Autowired
    private SupplierServiceImpl supplierService;

    @GetMapping
    public List<SupplierResponse> getAllSuppliers() {
        return supplierService.findAll();
    }

    @GetMapping("/{id}")
    public SupplierResponse getSupplierById(@PathVariable Integer id) {
        return supplierService.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    @PostMapping
    public SupplierResponse createSupplier(@RequestBody SupplierRequest request) {
        return supplierService.createSupplier(request);
    }

    @PutMapping("/{id}")
    public SupplierResponse updateSupplier(@PathVariable Integer id, @RequestBody SupplierRequest request) {
        return supplierService.updateSupplier(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable Integer id) {
        supplierService.deleteSupplier(id);
    }
}
