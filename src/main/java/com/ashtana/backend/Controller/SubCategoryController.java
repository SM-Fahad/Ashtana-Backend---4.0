package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.SubCategoryRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.SubCategoryResponseDTO;
import com.ashtana.backend.Service.SubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    // ✅ Create SubCategory
    @PostMapping
    public ResponseEntity<SubCategoryResponseDTO> createSubCategory(
            @Valid @RequestBody SubCategoryRequestDTO dto) {
        SubCategoryResponseDTO response = subCategoryService.createSubCategory(dto);
        return ResponseEntity.status(201).body(response);
    }

    // ✅ Get All SubCategories
    @GetMapping
    public ResponseEntity<List<SubCategoryResponseDTO>> getAllSubCategories() {
        List<SubCategoryResponseDTO> subCategories = subCategoryService.getAllSubCategories();
        return ResponseEntity.ok(subCategories);
    }

    // ✅ Get SubCategory by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubCategoryResponseDTO> getSubCategoryById(@PathVariable Long id) {
        SubCategoryResponseDTO response = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(response);
    }

    // ✅ Update SubCategory
    @PutMapping("/{id}")
    public ResponseEntity<SubCategoryResponseDTO> updateSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryRequestDTO dto) {
        SubCategoryResponseDTO response = subCategoryService.updateSubCategory(id, dto);
        return ResponseEntity.ok(response);
    }

    // ✅ Delete SubCategory
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }
}
