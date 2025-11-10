package com.ashtana.backend.Entity;

import com.ashtana.backend.Enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false, unique = true)
    private String sku;

    // Many-to-Many with Size (replacing old @ManyToOne)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_sizes",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    @JsonIgnoreProperties("products")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Size> availableSizes = new HashSet<>();

    // Many-to-Many with Color (replacing old @ManyToOne)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_colors",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "color_id")
    )
    @JsonIgnoreProperties("products")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Color> availableColors = new HashSet<>();

    // One-to-Many with FileData for images (replacing old List<String> imageUrls)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FileData> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products")
//    @ToString.Exclude
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    @JsonIgnoreProperties("products")
//    @ToString.Exclude
    private SubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    // Additional production fields
    private Double weight;
    private String dimensions;
    private String material;
    private String brand;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_best_seller")
    private Boolean isBestSeller = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods for managing relationships
    public void addImage(FileData image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(FileData image) {
        images.remove(image);
        image.setProduct(null);
    }

    public void addSize(Size size) {
        availableSizes.add(size);
        size.getProducts().add(this);
    }

    public void removeSize(Size size) {
        availableSizes.remove(size);
        size.getProducts().remove(this);
    }

    public void addColor(Color color) {
        availableColors.add(color);
        color.getProducts().add(this);
    }

    public void removeColor(Color color) {
        availableColors.remove(color);
        color.getProducts().remove(this);
    }

    // Convenience method to get primary image
    public FileData getPrimaryImage() {
        return images.stream()
                .filter(FileData::getIsPrimary)
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }
}