package com.ashtana.backend.Controller;

import com.ashtana.backend.Entity.Coupon;
import com.ashtana.backend.Service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        return couponService.createCoupon(coupon);
    }

    @GetMapping("/validate")
    public Double validateCoupon(
            @RequestParam String code,
            @RequestParam Double orderAmount) {
        return couponService.calculateDiscount(code, orderAmount);
    }

    @GetMapping
    public List<Coupon> getActiveCoupons() {
        return couponService.getAllActiveCoupons();
    }

    @PutMapping("/{id}")
    public Coupon updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        return couponService.updateCoupon(id, coupon);
    }

    @DeleteMapping("/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return "Coupon deleted successfully";
    }
}