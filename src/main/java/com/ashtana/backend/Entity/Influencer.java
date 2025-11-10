package com.ashtana.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "influencers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Influencer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "influencer_code", unique = true, nullable = false)
    private String influencerCode;

    @Column(name = "commission_rate", nullable = false)
    private Double commissionRate = 10.0;

    @Column(name = "total_earnings", nullable = false)
    private Double totalEarnings = 0.0;

    @Column(name = "pending_earnings", nullable = false)
    private Double pendingEarnings = 0.0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_name", referencedColumnName = "userName", nullable = false, unique = true)
    private User user;

    // Initialize collections to avoid null pointers
    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Coupon> coupons = new ArrayList<>();

    @OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Commission> commissions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.influencerCode == null) {
            this.influencerCode = "INF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        // Ensure collections are initialized
        if (this.coupons == null) {
            this.coupons = new ArrayList<>();
        }
        if (this.commissions == null) {
            this.commissions = new ArrayList<>();
        }
    }
}