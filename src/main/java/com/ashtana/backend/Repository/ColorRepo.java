package com.ashtana.backend.Repository;



import com.ashtana.backend.Entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ColorRepo extends JpaRepository<Color, Long> {
    List<Color> findByProducts_Id(Long productId);
}
