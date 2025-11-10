package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.OrderRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.OrderItemResponseDTO;
import com.ashtana.backend.DTO.ResponseDTO.OrderResponseDTO;
import com.ashtana.backend.Entity.*;
import com.ashtana.backend.Enums.OrderStatus;
import com.ashtana.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final AddressRepo addressRepo;
    private final MyBagItemsRepo myBagItemsRepo;
    private final OrderItemRepo orderItemRepo;
    private final CouponRepo couponRepo;
    private final MyBagService myBagService;
    private final CommissionService commissionService;

    @Transactional
    public OrderResponseDTO checkout(String userName, Long shippingAddressId, Long billingAddressId, String couponCode, String paymentMethod) {
        // 1. Get user and validate
        User user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found: " + userName));

        // 2. Get user's bag
        MyBag myBag = myBagService.getOrCreateBag(userName);
        if (myBag.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Add products before checkout.");
        }

        // 3. Get addresses
        Address shippingAddress = addressRepo.findById(shippingAddressId)
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));

        Address billingAddress = (billingAddressId != null) ?
                addressRepo.findById(billingAddressId)
                        .orElseThrow(() -> new RuntimeException("Billing address not found")) :
                shippingAddress;

        // 4. Create order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        order.setStatus(OrderStatus.PENDING);

        // 5. Convert bag items to order items
        for (MyBagItems bagItem : myBag.getItems()) {
            OrderItem orderItem = OrderItem.fromMyBagItem(bagItem);
            order.addOrderItem(orderItem);
        }

        // 6. Apply coupon if provided
        Coupon appliedCoupon = null;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            appliedCoupon = couponRepo.findByCodeAndActiveTrue(couponCode)
                    .orElseThrow(() -> new RuntimeException("Invalid or expired coupon code"));
            order.applyCoupon(appliedCoupon);

            // Update coupon usage
            appliedCoupon.incrementUsage(order.getDiscountAmount());
            couponRepo.save(appliedCoupon);
        }

        // 7. Calculate totals
        order.calculateTotals();

        // 8. Save order
        Order savedOrder = orderRepo.save(order);

        // 9. Create commission if influencer coupon was used
        if (appliedCoupon != null && appliedCoupon.getInfluencer() != null) {
            commissionService.createCommission(savedOrder, appliedCoupon);
        }

        // 10. Clear user's bag
        myBag.getItems().clear();
        myBag.setTotalPrice(0.0);
        myBagService.recalculateTotal(myBag);

        // Set payment method and status
        order.setPaymentMethod(paymentMethod);

        if ("COD".equalsIgnoreCase(paymentMethod)) {
            order.setPaymentStatus("PENDING"); // Will be marked as PAID when delivered
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            order.setPaymentStatus("PENDING"); // Will redirect to payment gateway
            order.setStatus(OrderStatus.PENDING_PAYMENT);
        }

        return toDto(savedOrder);
    }

    public OrderResponseDTO applyCouponToOrder(Long orderId, String couponCode) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Coupon coupon = couponRepo.findByCodeAndActiveTrue(couponCode)
                .orElseThrow(() -> new RuntimeException("Invalid or expired coupon code"));

        order.applyCoupon(coupon);

        // Update coupon usage
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepo.save(coupon);

        Order updatedOrder = orderRepo.save(order);
        return toDto(updatedOrder);
    }

    public OrderResponseDTO removeCouponFromOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.removeCoupon();
        Order updatedOrder = orderRepo.save(order);
        return toDto(updatedOrder);
    }

    // Convert Entity to DTO
    public OrderResponseDTO toDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserName(order.getUser().getUserName());
        dto.setSubtotalAmount(order.getSubtotalAmount());
        dto.setShippingCost(order.getShippingCost());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        dto.setCouponCode(order.getCouponCode());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());

        // Convert order items
        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream().map(item -> {
                OrderItemResponseDTO itemDto = new OrderItemResponseDTO();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProductName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPricePerItem(item.getPrice());
                itemDto.setTotalPrice(item.getTotalPrice());
                return itemDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    public OrderResponseDTO findById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toDto(order);
    }

    public List<OrderResponseDTO> findByUserName(String userName) {
        return orderRepo.findByUserUserName(userName).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDTO> findAll() {
        return orderRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO updateStatus(Long id, OrderStatus status) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepo.save(order);
        return toDto(updatedOrder);
    }

    public void delete(Long id) {
        orderRepo.deleteById(id);
    }
}