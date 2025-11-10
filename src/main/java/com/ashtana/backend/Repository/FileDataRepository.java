package com.ashtana.backend.Repository;


import com.ashtana.backend.Entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileDataRepository extends JpaRepository<FileData, Long> {

    List<FileData> findByProductId(Long productId);

    List<FileData> findByProductIdAndIsActiveTrue(Long productId);

    Optional<FileData> findByProductIdAndIsPrimaryTrue(Long productId);

    @Query("SELECT fd FROM FileData fd WHERE fd.product.id = :productId ORDER BY fd.sortOrder ASC")
    List<FileData> findProductImagesOrdered(@Param("productId") Long productId);

    Optional<FileData> findByFileName(String fileName);

    Optional<FileData> findByChecksum(String checksum);
}