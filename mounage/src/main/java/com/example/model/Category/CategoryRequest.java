package com.example.model.Category;

import lombok.Data;

@Data
public class CategoryRequest {
    private String categoryName;
    private Long parentCategoryId;
    private String description;
    private Boolean isActive;
}
