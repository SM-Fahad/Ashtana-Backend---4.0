package com.ashtana.backend.Repository;

import com.ashtana.backend.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUserUserName(String userName);
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o WHERE o.user.userName = :userName ORDER BY o.orderDate DESC")
    List<Order> findUserOrderHistory(@Param("userName") String userName);
}