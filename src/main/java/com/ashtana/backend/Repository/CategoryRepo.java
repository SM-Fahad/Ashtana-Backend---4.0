package com.ashtana.backend.Repository;

import com.ashtana.backend.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
