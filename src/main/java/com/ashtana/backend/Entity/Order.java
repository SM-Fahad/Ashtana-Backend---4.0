package com.ashtana.backend.Entity;

import com.ashtana.backend.Enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name", referencedColumnName = "userName", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @Column(name = "subtotal_amount", nullable = false)
    private Double subtotalAmount = 0.0;

    @Column(name = "shipping_cost", nullable = false)
    private Double shippingCost = 0.0;

    @Column(name = "tax_amount", nullable = false)
    private Double taxAmount = 0.0;

    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount = 0.0;

    @Column(name = "payment_method")
    private String paymentMethod; // "COD", "CREDIT_CARD", "PAYPAL", etc.

    @Column(name = "payment_status")
    private String paymentStatus = "PENDING"; // PENDING, PAID, FAILED, REFUNDED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "coupon_code")
    private String couponCode;



    // Business logic methods
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }



    public void calculateTotals() {
        // Calculate subtotal (items total)
        this.subtotalAmount = orderItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();

        // Calculate shipping ($30 free above $150)
        this.shippingCost = (this.subtotalAmount >= 150.0) ? 0.0 : 30.0;

        // Calculate taxes (5% GST + 7% PST = 12% total)
        double taxableAmount = this.subtotalAmount - this.discountAmount;
        this.taxAmount = taxableAmount * 0.12;

        // Calculate final total
        this.totalAmount = this.subtotalAmount + this.shippingCost + this.taxAmount - this.discountAmount;
    }

    public void applyCoupon(Coupon coupon) {
        if (coupon != null && coupon.isValid()) {
            this.coupon = coupon;
            this.couponCode = coupon.getCode();
            this.discountAmount = this.subtotalAmount * (coupon.getDiscountPercentage() / 100);
            calculateTotals();
        }
    }

    public void removeCoupon() {
        this.coupon = null;
        this.couponCode = null;
        this.discountAmount = 0.0;
        calculateTotals();
    }

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
        if (this.orderNumber == null) {
            // Generate order number with timestamp: ORD-YYYYMMDD-XXXXX
            String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            String random = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            this.orderNumber = "ORD-" + timestamp + "-" + random;
        }
        calculateTotals();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateTotals();
    }
}