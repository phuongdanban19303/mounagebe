package com.example.service.Impl;

import com.example.entity.Supplier;
import com.example.model.Supplier.SupplierRequest;
import com.example.model.Supplier.SupplierResponse;
import com.example.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class SupplierServiceImpl {
    @Autowired
    private SupplierRepository supplierRepository;

    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<SupplierResponse> findById(Integer id) {
        return supplierRepository.findById(Long.valueOf(id)).map(this::toResponse);
    }

    public SupplierResponse createSupplier(SupplierRequest req) {
        Supplier supplier = new Supplier();
        mapRequestToEntity(req, supplier);
        supplier.setCreatedAt(Instant.now());
        supplier.setUpdatedAt(Instant.now());
        return toResponse(supplierRepository.save(supplier));
    }

    public SupplierResponse updateSupplier(Integer id, SupplierRequest req) {
        Supplier supplier = supplierRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        mapRequestToEntity(req, supplier);
        supplier.setUpdatedAt(Instant.now());
        return toResponse(supplierRepository.save(supplier));
    }

    public void deleteSupplier(Integer id) {
        supplierRepository.deleteById(Long.valueOf(id));
    }

    private void mapRequestToEntity(SupplierRequest req, Supplier supplier) {
        supplier.setSupplierCode(req.getSupplierCode());
        supplier.setSupplierName(req.getSupplierName());
        supplier.setContactPerson(req.getContactPerson());
        supplier.setPhone(req.getPhone());
        supplier.setEmail(req.getEmail());
        supplier.setAddress(req.getAddress());
        supplier.setTaxCode(req.getTaxCode());
        supplier.setPaymentTerms(req.getPaymentTerms());
        supplier.setIsActive(req.getIsActive());
    }

    private SupplierResponse toResponse(Supplier supplier) {
        SupplierResponse res = new SupplierResponse();
        res.setId(supplier.getId());
        res.setSupplierCode(supplier.getSupplierCode());
        res.setSupplierName(supplier.getSupplierName());
        res.setContactPerson(supplier.getContactPerson());
        res.setPhone(supplier.getPhone());
        res.setEmail(supplier.getEmail());
        res.setAddress(supplier.getAddress());
        res.setTaxCode(supplier.getTaxCode());
        res.setPaymentTerms(supplier.getPaymentTerms());
        res.setIsActive(supplier.getIsActive());
        res.setCreatedAt(supplier.getCreatedAt());
        res.setUpdatedAt(supplier.getUpdatedAt());
        return res;
    }
}
