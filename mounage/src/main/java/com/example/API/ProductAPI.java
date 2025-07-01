package com.example.API;

import com.example.entity.Product;
import com.example.model.ProductRequest;
import com.example.model.ProductResponse;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Validated
@CrossOrigin("*")
public class ProductAPI {

    @Autowired
    private ProductService productService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody @Valid ProductRequest productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(productDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                 @RequestBody @Valid ProductRequest dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String barcode,
                                                @RequestParam(required = false) Long categoryId,
                                                @RequestParam(required = false) Long supplierId) {
        return ResponseEntity.ok(productService.searchProducts(name, barcode, categoryId, supplierId));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<ProductResponse>> autocomplete(@RequestParam(required = false) String key) {
        return ResponseEntity.ok(productService.autocompleteProducts(key));
    }
}
