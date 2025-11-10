package com.ashtana.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "size")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sizeName;

    private String description;

    @Column(name = "size_code", unique = true)
    private String sizeCode;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Many-to-Many relationship with Product
    @ManyToMany(mappedBy = "availableSizes")
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    // Helper method for adding product
    public void addProduct(Product product) {
        this.products.add(product);
        product.getAvailableSizes().add(this);
    }

    // Helper method for removing product
    public void removeProduct(Product product) {
        this.products.remove(product);
        product.getAvailableSizes().remove(this);
    }
}