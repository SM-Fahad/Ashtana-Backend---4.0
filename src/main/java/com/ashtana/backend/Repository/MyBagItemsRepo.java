package com.ashtana.backend.Repository;

import com.ashtana.backend.Entity.MyBagItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyBagItemsRepo extends JpaRepository<MyBagItems, Long> {
    List<MyBagItems> findByMyBagId(Long myBagId);

    // ✅ Remove this method - it's incorrect since MyBagItems doesn't have direct user reference
    // List<MyBagItems> findByUser_UserName(String userName);
}