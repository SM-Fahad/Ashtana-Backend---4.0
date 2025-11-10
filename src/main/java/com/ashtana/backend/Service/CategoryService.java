package com.ashtana.backend.Service;


import com.ashtana.backend.DTO.RequestDTO.CategoryRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.CategoryResponseDTO;
import com.ashtana.backend.Entity.Category;
import com.ashtana.backend.Repository.CategoryRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;


    private CategoryResponseDTO toDto(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }


    private Category toEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }


    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        boolean exists = categoryRepo.findAll().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(dto.getName()));
        if (exists) throw new RuntimeException("Category name already exists!");

        Category saved = categoryRepo.save(toEntity(dto));
        return toDto(saved);
    }


    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        return toDto(category);
    }


    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category existing = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());

        Category updated = categoryRepo.save(existing);
        return toDto(updated);
    }


    public String deleteCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        categoryRepo.delete(category);
        return "Category deleted successfully!";
    }
}
