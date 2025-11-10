package com.ashtana.backend.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "commissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, PAID, CANCELLED

    @Column(name = "commission_rate", nullable = false)
    private Double commissionRate;

    @Column(name = "order_amount", nullable = false)
    private Double orderAmount;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_id", nullable = false)
    @JsonBackReference
    private Influencer influencer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Business logic methods
    public void markAsPaid() {
        this.status = "PAID";
        this.paidAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    // Static factory method
    public static Commission createFromOrder(Order order, Coupon coupon, Influencer influencer) {
        Commission commission = new Commission();
        commission.setInfluencer(influencer);
        commission.setOrder(order);
        commission.setCoupon(coupon);
        commission.setOrderAmount(order.getSubtotalAmount());
        commission.setCommissionRate(influencer.getCommissionRate());
        commission.setOrderNumber(order.getOrderNumber());

        // Calculate commission amount
        Double commissionAmount = order.getSubtotalAmount() * (influencer.getCommissionRate() / 100);
        commission.setAmount(commissionAmount);

        return commission;
    }
}