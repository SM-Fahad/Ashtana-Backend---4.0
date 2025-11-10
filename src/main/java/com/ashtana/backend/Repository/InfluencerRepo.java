package com.ashtana.backend.Repository;

import com.ashtana.backend.Entity.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InfluencerRepo extends JpaRepository<Influencer, Long> {
    Optional<Influencer> findByInfluencerCode(String influencerCode);
    Optional<Influencer> findByUserUserName(String userName);
    boolean existsByUserUserName(String userName);

    // Add this if you want to use repository filtering
    List<Influencer> findByIsActiveTrue();
}