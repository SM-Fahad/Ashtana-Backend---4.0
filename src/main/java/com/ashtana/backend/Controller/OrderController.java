package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.ResponseDTO.OrderResponseDTO;
import com.ashtana.backend.Enums.OrderStatus;
import com.ashtana.backend.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

//    @PostMapping("/checkout")
//    public OrderResponseDTO checkout(
//            @RequestParam String userName,
//            @RequestParam Long shippingAddressId,
//            @RequestParam(required = false) Long billingAddressId,
//            @RequestParam(required = false) String couponCode) {
//        return orderService.checkout(userName, shippingAddressId, billingAddressId, couponCode);
//    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(
            @RequestParam String userName,
            @RequestParam Long shippingAddressId,
            @RequestParam(required = false) Long billingAddressId,
            @RequestParam(required = false) String couponCode,
            @RequestParam String paymentMethod) { // ✅ Add payment method

        // Validate payment method
        if (!isValidPaymentMethod(paymentMethod)) {
            throw new RuntimeException("Invalid payment method: " + paymentMethod);
        }

        return ResponseEntity.ok(orderService.checkout(userName, shippingAddressId, billingAddressId, couponCode, paymentMethod));
    }

    private boolean isValidPaymentMethod(String method) {
        return List.of("COD", "CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "STRIPE").contains(method.toUpperCase());
    }

    @PutMapping("/{orderId}/apply-coupon")
    public OrderResponseDTO applyCoupon(
            @PathVariable Long orderId,
            @RequestParam String couponCode) {
        return orderService.applyCouponToOrder(orderId, couponCode);
    }

    @PutMapping("/{orderId}/remove-coupon")
    public OrderResponseDTO removeCoupon(@PathVariable Long orderId) {
        return orderService.removeCouponFromOrder(orderId);
    }

    @GetMapping("/user/{userName}")
    public List<OrderResponseDTO> getUserOrders(@PathVariable String userName) {
        return orderService.findByUserName(userName);
    }

    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PutMapping("/{id}/status")
    public OrderResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return "Order deleted successfully!";
    }
}