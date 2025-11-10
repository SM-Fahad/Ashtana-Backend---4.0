package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.PaymentRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.PaymentResponseDTO;
import com.ashtana.backend.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Initiate payment
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    // For COD orders
    @PostMapping("/cod/{orderId}")
    public ResponseEntity<PaymentResponseDTO> confirmCOD(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.confirmCOD(orderId));
    }

    // Payment success callback (for payment gateways)
    @PostMapping("/success")
    public ResponseEntity<PaymentResponseDTO> paymentSuccess(@RequestParam String paymentId) {
        return ResponseEntity.ok(paymentService.markPaymentAsPaid(paymentId));
    }

    // ✅ FIXED: Payment failure callback
    @PostMapping("/failure")
    public ResponseEntity<PaymentResponseDTO> paymentFailure(@RequestParam String paymentId) {
        return ResponseEntity.ok(paymentService.markPaymentAsFailed(paymentId));
    }

    // Get payment status
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentStatus(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(paymentId));
    }

    // Get payment by order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    // Cancel payment
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponseDTO> cancelPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.cancelPayment(paymentId));
    }
}