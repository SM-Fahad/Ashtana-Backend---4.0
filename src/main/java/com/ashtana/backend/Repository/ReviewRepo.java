package com.ashtana.backend.Repository;


import com.ashtana.backend.Entity.MyBagItems;
import com.ashtana.backend.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
//    List<Review> findByUserId(Long userId);
    List<Review> findByUser_UserName(String userName);

}
