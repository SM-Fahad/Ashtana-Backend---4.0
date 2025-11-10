package com.ashtana.backend.Repository;



import com.ashtana.backend.Entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeRepo extends JpaRepository<Size, Long> {
    List<Size> findByProducts_Id(Long productId);

}
