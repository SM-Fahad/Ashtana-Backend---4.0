package com.ashtana.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double price; // Price at time of order

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore // Prevent infinite recursion
    private Order order;

    // ✅ Product name snapshot (in case product name changes later)
    @Column(name = "product_name", nullable = false)
    private String productName;

    // ✅ Product image snapshot (optional but useful)
    @Column(name = "product_image")
    private String productImage;

    // ✅ Product SKU snapshot (optional)
    @Column(name = "product_sku")
    private String productSku;

    // ✅ Automatically calculate total price before save/update
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (this.price == null && this.product != null) {
            this.price = this.product.getPrice();
        }

        if (this.price != null && this.quantity != null) {
            this.totalPrice = this.price * this.quantity;
        }

        // Snapshot product details
        if (this.product != null) {
            if (this.productName == null) {
                this.productName = this.product.getName();
            }
            // Uncomment if you have these fields in Product entity
            /*
            if (this.productImage == null) {
                this.productImage = this.product.getImageUrl();
            }
            if (this.productSku == null) {
                this.productSku = this.product.getSku();
            }
            */
        }
    }

    // ✅ Static factory method to create from MyBagItems
    public static OrderItem fromMyBagItem(MyBagItems bagItem) {
        if (bagItem == null || bagItem.getProduct() == null) {
            throw new IllegalArgumentException("Bag item or product cannot be null");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(bagItem.getProduct());
        orderItem.setQuantity(bagItem.getQuantity());
        orderItem.setPrice(bagItem.getProduct().getPrice()); // Capture current price
        orderItem.setProductName(bagItem.getProduct().getName());

        // Calculate total price
        orderItem.calculateTotalPrice();

        return orderItem;
    }

    // ✅ Helper method to update quantity
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.quantity = newQuantity;
        calculateTotalPrice(); // Recalculate total
    }

    // ✅ Helper method to get item summary
    public String getItemSummary() {
        return String.format("%s x%d - $%.2f", productName, quantity, totalPrice);
    }
}