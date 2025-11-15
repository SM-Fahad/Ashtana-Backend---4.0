package com.ashtana.backend.Repository;

import com.ashtana.backend.Entity.MyBagItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyBagItemsRepo extends JpaRepository<MyBagItems, Long> {

    List<MyBagItems> findByMyBagId(Long bagId);

    Optional<MyBagItems> findByMyBagIdAndProductId(Long bagId, Long productId);

    // Add this method if not exists
    void deleteById(Long id);

    // Optional: Native query for safe deletion
    @Modifying
    @Query("DELETE FROM MyBagItems m WHERE m.id = :id")
    void deleteMyBagItemById(@Param("id") Long id);
}