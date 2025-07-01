package com.example.service.Impl;

import com.example.entity.Category;
import com.example.model.Category.CategoryRequest;
import com.example.model.Category.CategoryResponse;
import com.example.repository.CategoryRepository;
import com.example.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepo, ModelMapper modelMapper) {
        this.categoryRepo = categoryRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest dto) {

        Category category = modelMapper.map(dto, Category.class);

        if (dto.getParentCategoryId() != null) {
            Category parent = categoryRepo.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Danh mục cha không tồn tại"));
            category.setParentCategory(parent);
        }

        Category saved = categoryRepo.save(category);

        return toResponse(saved);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest dto) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        category.setIsActive(dto.getIsActive());

        if (dto.getParentCategoryId() != null) {
            Category parent = categoryRepo.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Danh mục cha không tồn tại"));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        Category updated = categoryRepo.save(category);
        return toResponse(updated);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepo.existsById(id)) {
            throw new RuntimeException("Danh mục không tồn tại");
        }
        categoryRepo.deleteById(id);
    }

    @Override

    public List<CategoryResponse> getAllCategories() {
        try {
            return categoryRepo.findAll().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }catch(Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách danh mục: " + e.getMessage(), e);
        }

    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        return toResponse(category);
    }

    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = modelMapper.map(category, CategoryResponse.class);
        if (category.getParentCategory() != null) {
            response.setParentCategoryId(category.getParentCategory().getId());
        }
        if (category.getCreatedAt() != null) {
            response.setCreatedAt(LocalDateTime.ofInstant(category.getCreatedAt(), ZoneId.systemDefault()));
        }
        if (category.getUpdatedAt() != null) {
            response.setUpdatedAt(LocalDateTime.ofInstant(category.getUpdatedAt(), ZoneId.systemDefault()));
        }
        return response;
    }
}
