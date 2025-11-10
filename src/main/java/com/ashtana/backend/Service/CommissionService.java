package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.ResponseDTO.CommissionResponseDTO;
import com.ashtana.backend.Entity.Commission;
import com.ashtana.backend.Entity.Influencer;
import com.ashtana.backend.Entity.Order;
import com.ashtana.backend.Entity.Coupon;
import com.ashtana.backend.Repository.CommissionRepo;
import com.ashtana.backend.Repository.InfluencerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepo commissionRepo;
    private final InfluencerRepo influencerRepo;

    public Commission createCommission(Order order, Coupon coupon) {
        if (coupon == null || coupon.getInfluencer() == null) {
            return null; // No commission for non-influencer coupons
        }

        Influencer influencer = coupon.getInfluencer();
        Commission commission = Commission.createFromOrder(order, coupon, influencer);

        Commission savedCommission = commissionRepo.save(commission);

        // ✅ FIXED: Update influencer earnings - direct field access
        Double commissionAmount = savedCommission.getAmount();
        if (commissionAmount != null && commissionAmount > 0) {
            influencer.setPendingEarnings(influencer.getPendingEarnings() + commissionAmount);
            influencer.setTotalEarnings(influencer.getTotalEarnings() + commissionAmount);
            influencerRepo.save(influencer);
        }

        return savedCommission;
    }

    public List<CommissionResponseDTO> getCommissionsByInfluencerId(Long influencerId) {
        return commissionRepo.findByInfluencerId(influencerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CommissionResponseDTO> getCommissionsByInfluencerIdAndStatus(Long influencerId, String status) {
        return commissionRepo.findByInfluencerIdAndStatus(influencerId, status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CommissionResponseDTO> getPendingCommissions() {
        return commissionRepo.findByStatus("PENDING").stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CommissionResponseDTO markCommissionAsPaid(Long commissionId) {
        Commission commission = commissionRepo.findById(commissionId)
                .orElseThrow(() -> new RuntimeException("Commission not found"));

        // Mark commission as paid
        commission.setStatus("PAID");
        commission.setPaidAt(java.time.LocalDateTime.now());
        Commission updatedCommission = commissionRepo.save(commission);

        // ✅ FIXED: Release pending earnings - direct field access
        Influencer influencer = updatedCommission.getInfluencer();
        Double commissionAmount = updatedCommission.getAmount();

        if (influencer != null && commissionAmount != null && commissionAmount > 0) {
            if (influencer.getPendingEarnings() >= commissionAmount) {
                influencer.setPendingEarnings(influencer.getPendingEarnings() - commissionAmount);
            } else {
                // If pending is less than amount (shouldn't happen, but safety check)
                influencer.setPendingEarnings(0.0);
            }
            influencerRepo.save(influencer);
        }

        return toDto(updatedCommission);
    }

    public void payAllPendingCommissions(Long influencerId) {
        List<Commission> pendingCommissions = commissionRepo.findByInfluencerIdAndStatus(influencerId, "PENDING");

        Influencer influencer = influencerRepo.findById(influencerId)
                .orElseThrow(() -> new RuntimeException("Influencer not found"));

        Double totalPendingAmount = 0.0;

        for (Commission commission : pendingCommissions) {
            commission.setStatus("PAID");
            commission.setPaidAt(java.time.LocalDateTime.now());
            commissionRepo.save(commission);

            if (commission.getAmount() != null) {
                totalPendingAmount += commission.getAmount();
            }
        }

        // ✅ FIXED: Update influencer pending earnings
        if (totalPendingAmount > 0) {
            if (influencer.getPendingEarnings() >= totalPendingAmount) {
                influencer.setPendingEarnings(influencer.getPendingEarnings() - totalPendingAmount);
            } else {
                influencer.setPendingEarnings(0.0);
            }
            influencerRepo.save(influencer);
        }
    }

    public CommissionResponseDTO toDto(Commission commission) {
        if (commission == null) {
            return null;
        }

        CommissionResponseDTO dto = new CommissionResponseDTO();
        dto.setId(commission.getId());
        dto.setAmount(commission.getAmount());
        dto.setStatus(commission.getStatus());
        dto.setCommissionRate(commission.getCommissionRate());
        dto.setOrderAmount(commission.getOrderAmount());
        dto.setOrderNumber(commission.getOrderNumber());
        dto.setCreatedAt(commission.getCreatedAt());
        dto.setPaidAt(commission.getPaidAt());

        if (commission.getInfluencer() != null) {
            dto.setInfluencerId(commission.getInfluencer().getId());
            dto.setInfluencerCode(commission.getInfluencer().getInfluencerCode());
        }

        if (commission.getCoupon() != null) {
            dto.setCouponCode(commission.getCoupon().getCode());
        }

        if (commission.getOrder() != null) {
            dto.setOrderId(commission.getOrder().getId());
        }

        return dto;
    }

    // ✅ Additional utility methods
    public Double getTotalPaidCommissions(Long influencerId) {
        Double total = commissionRepo.sumPaidCommissionsByInfluencerId(influencerId);
        return total != null ? total : 0.0;
    }

    public Double getTotalPendingCommissions(Long influencerId) {
        Double total = commissionRepo.sumPendingCommissionsByInfluencerId(influencerId);
        return total != null ? total : 0.0;
    }

    public CommissionResponseDTO getCommissionById(Long id) {
        Commission commission = commissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Commission not found"));
        return toDto(commission);
    }

    public List<CommissionResponseDTO> getAllCommissions() {
        return commissionRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Cancel a commission (if needed)
    public CommissionResponseDTO cancelCommission(Long commissionId) {
        Commission commission = commissionRepo.findById(commissionId)
                .orElseThrow(() -> new RuntimeException("Commission not found"));

        commission.setStatus("CANCELLED");
        Commission cancelledCommission = commissionRepo.save(commission);

        // If commission was already paid, reverse the earnings
        if ("PAID".equals(commission.getStatus())) {
            Influencer influencer = commission.getInfluencer();
            Double commissionAmount = commission.getAmount();

            if (influencer != null && commissionAmount != null && commissionAmount > 0) {
                influencer.setTotalEarnings(influencer.getTotalEarnings() - commissionAmount);
                influencerRepo.save(influencer);
            }
        }

        return toDto(cancelledCommission);
    }
}