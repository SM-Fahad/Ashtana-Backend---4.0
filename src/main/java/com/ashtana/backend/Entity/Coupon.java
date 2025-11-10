package com.ashtana.backend.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @Column(name = "min_order_amount")
    private Double minOrderAmount = 0.0;

    @Column(name = "valid_from")
    private LocalDateTime validFrom = LocalDateTime.now();

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_id")
    @JsonBackReference
    private Influencer influencer;

    @Column(name = "coupon_type", nullable = false)
    private String couponType = "PERCENTAGE"; // PERCENTAGE, FIXED_AMOUNT

    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Column(name = "total_discount_given")
    private Double totalDiscountGiven = 0.0;

    // Business logic methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active &&
                (validFrom == null || now.isAfter(validFrom)) &&
                (validUntil == null || now.isBefore(validUntil)) &&
                (usageLimit == null || usedCount < usageLimit);
    }

    public Double calculateDiscount(Double orderAmount) {
        if (!isValid() || orderAmount < minOrderAmount) {
            return 0.0;
        }

        Double discount = 0.0;
        if ("PERCENTAGE".equals(couponType) && discountPercentage != null) {
            discount = orderAmount * (discountPercentage / 100);
            if (maxDiscount != null && discount > maxDiscount) {
                discount = maxDiscount;
            }
        } else if ("FIXED_AMOUNT".equals(couponType) && discountAmount != null) {
            discount = Math.min(discountAmount, orderAmount);
        }

        return discount;
    }

    public void incrementUsage(Double discountAmount) {
        this.usedCount++;
        this.totalOrders++;
        this.totalDiscountGiven += discountAmount;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.code == null) {
            // Generate coupon code: SAVE10-XXXX
            String random = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            if (discountPercentage != null) {
                this.code = "SAVE" + discountPercentage.intValue() + "-" + random;
            } else {
                this.code = "DEAL-" + random;
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Static factory method for influencer coupons
    public static Coupon createInfluencerCoupon(Influencer influencer, Double discountPercentage) {
        Coupon coupon = new Coupon();
        coupon.setInfluencer(influencer);
        coupon.setDiscountPercentage(discountPercentage);
        coupon.setCouponType("PERCENTAGE");
        coupon.setUsageLimit(100); // Default limit
        coupon.setValidUntil(LocalDateTime.now().plusMonths(6)); // 6 months validity
        coupon.setMinOrderAmount(50.0); // Minimum order amount
        return coupon;
    }
}