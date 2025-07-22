package com.example.service.Impl;

import com.example.entity.Category;
import com.example.entity.PackagingType;
import com.example.entity.Product;
import com.example.entity.Supplier;
import com.example.exception.ResourceNotFoundException;
import com.example.model.ProductRequest;
import com.example.model.ProductResponse;
import com.example.repository.CategoryRepository;
import com.example.repository.PackagingRepository;
import com.example.repository.ProductRepository;
import com.example.repository.SupplierRepository;
import com.example.service.Auditable;
import com.example.service.ProductService;
import com.example.service.SequenceService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired
    private PackagingRepository packagingRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SequenceService sequenceService;

    @Override
    @Auditable(action = "CREATE", tableName = "product")
    public ProductResponse addProduct(ProductRequest dto) {
        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại"));
        PackagingType packaging = packagingRepo.findById(dto.getPackageId())
                .orElseThrow(() -> new IllegalArgumentException("Loại bao bì không tồn tại"));
        Product product = modelMapper.map(dto, Product.class);
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setPackaging(packaging);

        if (dto.getProductCode() == null || dto.getProductCode().isBlank()) {
            product.setProductCode(sequenceService.getNextProductCode());
        }

        Product saved = productRepo.save(product);
        LOGGER.info("Thêm sản phẩm: {}", saved.getProductName());

        return convertToResponse(saved);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Auditable(action = "UPDATE", tableName = "product")
    public ProductResponse updateProduct(Long id, ProductRequest dto) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại"));

        modelMapper.map(dto, product);
        product.setCategory(category);
        product.setSupplier(supplier);

        Product updated = productRepo.save(product);
        LOGGER.info("Cập nhật sản phẩm: {}", updated.getProductName());

        return convertToResponse(updated);
    }

    @Override
    @Auditable(action = "DELETE", tableName = "product")
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        productRepo.delete(product);
        LOGGER.info("Đã xóa sản phẩm ID: {}", id);
    }

    @Override
    public List<ProductResponse> searchProducts(String name, String barcode, Long categoryId, Long supplierId) {
        return productRepo.search(name, barcode, categoryId, supplierId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> autocompleteProducts(String key) {
        return productRepo.autocompleteSearch(key).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ---------------------------
    //  HÀM DÙNG CHUNG: convertToResponse
    // ---------------------------
    private ProductResponse convertToResponse(Product product) {
        ProductResponse dto = modelMapper.map(product, ProductResponse.class);

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getCategoryName());
        }
        if (product.getSupplier() != null) {
            dto.setSupplierName(product.getSupplier().getSupplierName());
        }
        if (product.getPackaging() != null) {
            dto.setPackagingName(product.getPackaging().getPackagingName());
        }
        if (product.getCreatedAt() != null) {
            dto.setCreatedAt(LocalDateTime.ofInstant(product.getCreatedAt(), ZoneId.systemDefault()));
        }
        if (product.getUpdatedAt() != null) {
            dto.setUpdatedAt(LocalDateTime.ofInstant(product.getUpdatedAt(), ZoneId.systemDefault()));
        }

        return dto;
    }
}
