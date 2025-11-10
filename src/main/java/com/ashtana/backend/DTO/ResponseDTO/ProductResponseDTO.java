package com.ashtana.backend.DTO.ResponseDTO;

import com.ashtana.backend.Enums.ProductStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String sku;
    private ProductStatus status;

    // Category information
    private String categoryName;
    private String subCategoryName;

    // Many-to-Many relationships
    private Set<String> availableSizes;
    private Set<String> availableColors;

    // Simple image URLs (for backward compatibility)
    private List<String> imageUrls;

    // Detailed image information
    private List<ImageDetail> imageDetails;

    // Additional fields
    private Double weight;
    private String dimensions;
    private String material;
    private String brand;
    private Boolean isFeatured;
    private Boolean isBestSeller;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class ImageDetail {
        private Long id;
        private String url;
        private String altText;
        private Boolean isPrimary;
        private Integer sortOrder;
    }
}