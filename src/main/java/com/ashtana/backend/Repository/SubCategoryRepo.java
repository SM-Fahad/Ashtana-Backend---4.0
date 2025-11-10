package com.ashtana.backend.Repository;


import com.ashtana.backend.Entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepo extends JpaRepository<SubCategory, Long> {
    // Find all sub-categories of a main category
    List<SubCategory> findByCategoryId(Long categoryId);

    // Find sub-category by name and category
    List<SubCategory> findByCategoryIdAndName(Long categoryId, String name);


}
