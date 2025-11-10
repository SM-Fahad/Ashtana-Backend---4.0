package com.ashtana.backend.Repository;

import com.ashtana.backend.Entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepo extends JpaRepository<Commission, Long> {
    List<Commission> findByInfluencerId(Long influencerId);
    List<Commission> findByInfluencerIdAndStatus(Long influencerId, String status);
    List<Commission> findByStatus(String status);

    @Query("SELECT SUM(c.amount) FROM Commission c WHERE c.influencer.id = :influencerId AND c.status = 'PAID'")
    Double sumPaidCommissionsByInfluencerId(@Param("influencerId") Long influencerId);

    @Query("SELECT SUM(c.amount) FROM Commission c WHERE c.influencer.id = :influencerId AND c.status = 'PENDING'")
    Double sumPendingCommissionsByInfluencerId(@Param("influencerId") Long influencerId);

    // Optional: Find commissions by order
    List<Commission> findByOrderId(Long orderId);
}