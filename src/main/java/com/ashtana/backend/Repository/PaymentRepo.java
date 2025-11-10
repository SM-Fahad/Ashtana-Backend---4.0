package com.ashtana.backend.Repository;

import com.ashtana.backend.Entity.Payment;
import com.ashtana.backend.Enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByPaymentStatus(PaymentStatus status);
    boolean existsByOrderIdAndPaymentStatus(Long orderId, PaymentStatus status);
}