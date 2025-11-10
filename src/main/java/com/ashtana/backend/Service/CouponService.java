package com.ashtana.backend.Service;

import com.ashtana.backend.Entity.Coupon;
import com.ashtana.backend.Repository.CouponRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepo couponRepo;

    public Coupon createCoupon(Coupon coupon) {
        if (couponRepo.existsByCode(coupon.getCode())) {
            throw new RuntimeException("Coupon code already exists");
        }
        return couponRepo.save(coupon);
    }

    public Optional<Coupon> validateCoupon(String code, Double orderAmount) {
        return couponRepo.findByCodeAndActiveTrue(code)
                .filter(coupon -> coupon.isValid() && orderAmount >= coupon.getMinOrderAmount());
    }

    public Double calculateDiscount(String code, Double orderAmount) {
        return validateCoupon(code, orderAmount)
                .map(coupon -> coupon.calculateDiscount(orderAmount))
                .orElse(0.0);
    }

    public List<Coupon> getAllActiveCoupons() {
        return couponRepo.findAll().stream()
                .filter(Coupon::isValid)
                .collect(java.util.stream.Collectors.toList());
    }

    public Coupon updateCoupon(Long id, Coupon couponDetails) {
        Coupon coupon = couponRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        coupon.setDiscountPercentage(couponDetails.getDiscountPercentage());
        coupon.setDiscountAmount(couponDetails.getDiscountAmount());
        coupon.setMaxDiscount(couponDetails.getMaxDiscount());
        coupon.setMinOrderAmount(couponDetails.getMinOrderAmount());
        coupon.setValidFrom(couponDetails.getValidFrom());
        coupon.setValidUntil(couponDetails.getValidUntil());
        coupon.setUsageLimit(couponDetails.getUsageLimit());
        coupon.setActive(couponDetails.getActive());

        return couponRepo.save(coupon);
    }

    public void deleteCoupon(Long id) {
        couponRepo.deleteById(id);
    }
}