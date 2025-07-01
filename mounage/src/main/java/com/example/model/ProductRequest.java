package com.example.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;

    private String description;

    private String productCode;

    @NotNull(message = "ID danh mục là bắt buộc")
    private Long categoryId;

    @NotNull(message = "ID nhà cung cấp là bắt buộc")
    private Long supplierId;
    @NotNull(message = "ID nhà quy cách định là bắt buộc")
    private Long packageId;


    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá vốn không được âm")
    private BigDecimal costPrice;

    private String barcode;

    private Integer minStockLevel = 0;
    private Integer maxStockLevel = 1000;

    private Integer reorderPoint;
    private Integer reorderQuantity;
    private Boolean hasExpiryDate;
    private Integer shelfLifeDays;

}
