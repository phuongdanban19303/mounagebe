package com.example.service;

import com.example.model.Category.CategoryRequest;
import com.example.model.Category.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest dto);
    CategoryResponse updateCategory(Long id, CategoryRequest dto);
    void deleteCategory(Long id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
}
