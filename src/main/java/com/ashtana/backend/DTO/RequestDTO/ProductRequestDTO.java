package com.ashtana.backend.DTO.RequestDTO;

import com.ashtana.backend.Enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stock;

    private String sku;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Long subCategoryId;
    private String userName;
    private ProductStatus status = ProductStatus.ACTIVE;

    // Many-to-Many relationships
    private Set<Long> sizeIds;
    private Set<Long> colorIds;

    // Additional fields
    private Double weight;
    private String dimensions;
    private String material;
    private String brand;
    private Boolean isFeatured = false;
    private Boolean isBestSeller = false;
}