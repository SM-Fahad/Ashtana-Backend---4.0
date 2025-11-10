package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.PaymentRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.PaymentResponseDTO;
import com.ashtana.backend.Entity.Order;
import com.ashtana.backend.Entity.Payment;
import com.ashtana.backend.Enums.OrderStatus;
import com.ashtana.backend.Enums.PaymentStatus;
import com.ashtana.backend.Repository.OrderRepo;
import com.ashtana.backend.Repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;

    public PaymentResponseDTO initiatePayment(PaymentRequestDTO request) {
        Order order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Create payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepo.save(payment);

        // Update order status
        if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
            order.setPaymentStatus("CONFIRMED");
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            order.setPaymentStatus("PENDING");
            order.setStatus(OrderStatus.PENDING_PAYMENT);

            // Here you would integrate with payment gateway
            // For now, we'll simulate payment success after 2 seconds
            simulatePaymentGateway(savedPayment);
        }

        orderRepo.save(order);

        return toDto(savedPayment);
    }

    public PaymentResponseDTO confirmCOD(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());

        order.setPaymentStatus("PAID");
        order.setStatus(OrderStatus.CONFIRMED);

        paymentRepo.save(payment);
        orderRepo.save(order);

        return toDto(payment);
    }

    public PaymentResponseDTO markPaymentAsPaid(String paymentId) {
        Payment payment = paymentRepo.findById(Long.parseLong(paymentId))
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());

        Order order = payment.getOrder();
        order.setPaymentStatus("PAID");
        order.setStatus(OrderStatus.CONFIRMED);

        paymentRepo.save(payment);
        orderRepo.save(order);

        return toDto(payment);
    }

    // ✅ ADD THIS MISSING METHOD
    public PaymentResponseDTO markPaymentAsFailed(String paymentId) {
        Payment payment = paymentRepo.findById(Long.parseLong(paymentId))
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());

        Order order = payment.getOrder();
        order.setPaymentStatus("FAILED");
        order.setStatus(OrderStatus.PAYMENT_FAILED);

        paymentRepo.save(payment);
        orderRepo.save(order);

        return toDto(payment);
    }

    // ✅ ADD THIS METHOD FOR PAYMENT STATUS CHECK
    public PaymentResponseDTO getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return toDto(payment);
    }

    // ✅ ADD THIS METHOD TO GET PAYMENT BY ORDER ID
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return toDto(payment);
    }

    // ✅ ADD THIS METHOD TO CANCEL PAYMENT
    public PaymentResponseDTO cancelPayment(Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.CANCELLED);

        Order order = payment.getOrder();
        order.setPaymentStatus("CANCELLED");
        order.setStatus(OrderStatus.CANCELLED);

        paymentRepo.save(payment);
        orderRepo.save(order);

        return toDto(payment);
    }

    private void simulatePaymentGateway(Payment payment) {
        // Simulate payment gateway processing
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate 2 second processing

                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(LocalDateTime.now());

                Order order = payment.getOrder();
                order.setPaymentStatus("PAID");
                order.setStatus(OrderStatus.CONFIRMED);

                paymentRepo.save(payment);
                orderRepo.save(order);

            } catch (Exception e) {
                // If simulation fails, mark as failed
                payment.setPaymentStatus(PaymentStatus.FAILED);
                Order order = payment.getOrder();
                order.setPaymentStatus("FAILED");
                order.setStatus(OrderStatus.PAYMENT_FAILED);

                paymentRepo.save(payment);
                orderRepo.save(order);

                e.printStackTrace();
            }
        }).start();
    }

    private PaymentResponseDTO toDto(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }
}