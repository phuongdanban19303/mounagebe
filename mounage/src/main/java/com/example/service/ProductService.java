package com.example.service;

import com.example.entity.Product;
import com.example.model.ProductRequest;
import com.example.model.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse addProduct(ProductRequest dto);
    ProductResponse updateProduct(Long id, ProductRequest dto);
    void deleteProduct(Long id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> searchProducts(String name, String barcode, Long categoryId, Long supplierId);
    List<ProductResponse> autocompleteProducts(String key);
}
