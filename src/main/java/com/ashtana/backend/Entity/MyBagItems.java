package com.ashtana.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "my_bag_items")
public class MyBagItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_bag_id", nullable = false)
    private MyBag myBag;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    // ✅ Add this method to calculate total price automatically
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (this.product != null && this.quantity != null) {
            this.totalPrice = this.product.getPrice() * this.quantity;
        }
    }
}