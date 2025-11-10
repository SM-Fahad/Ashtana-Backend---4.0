package com.ashtana.backend.DTO.ResponseDTO;

import lombok.Data;

@Data
public class SubCategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String subCategoryImg;
    private Long categoryId;
    private String categoryName;
}
