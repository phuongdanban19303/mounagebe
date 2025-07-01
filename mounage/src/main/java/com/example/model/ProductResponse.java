package com.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private String productCode;       // Mã sản phẩm nội bộ
    private String barcode;           // Mã vạch sản phẩm (EAN/UPC)
    private String productName;       // Tên sản phẩm
    private String categoryName;          // Mã danh mục
    private String supplierName;          // Mã nhà cung cấp
    private String packagingName;
    private BigDecimal costPrice;     // Giá vốn nhập hàng
    private BigDecimal sellingPrice;  // Giá bán lẻ
    private boolean hasExpiryDate;    // Có quản lý hạn sử dụng
    private Integer shelfLifeDays;    // Số ngày sử dụng từ NSX
    private boolean isActive;         // Trạng thái kinh doanh
    private LocalDateTime createdAt;  // Thời gian tạo
    private LocalDateTime updatedAt;  // Thời gian cập nhật
}
