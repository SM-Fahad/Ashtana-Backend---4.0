package com.ashtana.backend.Repository;

import com.ashtana.backend.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    // ✅ Find all items for a specific order
    List<OrderItem> findByOrderId(Long orderId);

    // ✅ Find specific product in an order
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.product.id = :productId")
    OrderItem findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);

    // ✅ Check if product exists in order
    boolean existsByOrderIdAndProductId(Long orderId, Long productId);

    // ✅ Count items in an order
    long countByOrderId(Long orderId);

    // ✅ Calculate total quantity for an order
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Integer sumQuantityByOrderId(@Param("orderId") Long orderId);
}