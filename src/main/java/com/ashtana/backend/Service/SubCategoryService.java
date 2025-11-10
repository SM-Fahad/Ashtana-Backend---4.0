package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.SubCategoryRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.SubCategoryResponseDTO;
import com.ashtana.backend.Entity.Category;
import com.ashtana.backend.Entity.SubCategory;
import com.ashtana.backend.Repository.CategoryRepo;
import com.ashtana.backend.Repository.SubCategoryRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubCategoryService {

    private final SubCategoryRepo subCategoryRepo;
    private final CategoryRepo categoryRepo;

    // ✅ Create SubCategory
    public SubCategoryResponseDTO createSubCategory(SubCategoryRequestDTO dto) {
        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        SubCategory subCategory = new SubCategory();
        subCategory.setName(dto.getName());
        subCategory.setDescription(dto.getDescription());
        subCategory.setSubCategoryImg(dto.getSubCategoryImg());
        subCategory.setCategory(category);

        SubCategory saved = subCategoryRepo.save(subCategory);
        return mapToResponseDTO(saved);
    }

    // ✅ Get All SubCategories
    public List<SubCategoryResponseDTO> getAllSubCategories() {
        return subCategoryRepo.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get SubCategory by ID
    public SubCategoryResponseDTO getSubCategoryById(Long id) {
        SubCategory subCategory = subCategoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sub-category not found with ID: " + id));
        return mapToResponseDTO(subCategory);
    }

    // ✅ Update SubCategory
    public SubCategoryResponseDTO updateSubCategory(Long id, SubCategoryRequestDTO dto) {
        SubCategory subCategory = subCategoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sub-category not found with ID: " + id));

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        subCategory.setName(dto.getName());
        subCategory.setDescription(dto.getDescription());
        subCategory.setSubCategoryImg(dto.getSubCategoryImg());
        subCategory.setCategory(category);

        SubCategory updated = subCategoryRepo.save(subCategory);
        return mapToResponseDTO(updated);
    }

    // ✅ Delete SubCategory
    public void deleteSubCategory(Long id) {
        SubCategory subCategory = subCategoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sub-category not found with ID: " + id));
        subCategoryRepo.delete(subCategory);
    }

    // ✅ Helper method for mapping entity → DTO
    private SubCategoryResponseDTO mapToResponseDTO(SubCategory subCategory) {
        SubCategoryResponseDTO dto = new SubCategoryResponseDTO();
        dto.setId(subCategory.getId());
        dto.setName(subCategory.getName());
        dto.setDescription(subCategory.getDescription());
        dto.setSubCategoryImg(subCategory.getSubCategoryImg());
        dto.setCategoryId(subCategory.getCategory().getId());
        dto.setCategoryName(subCategory.getCategory().getName());
        return dto;
    }
}
