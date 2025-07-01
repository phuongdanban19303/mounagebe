package com.example.model.Category;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CategoryResponse {
    private Long id;
    private String categoryName;
    private Long parentCategoryId;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
