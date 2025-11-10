package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.OrderItemRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.OrderItemResponseDTO;
import com.ashtana.backend.Entity.Order;
import com.ashtana.backend.Entity.OrderItem;
import com.ashtana.backend.Entity.Product;
import com.ashtana.backend.Repository.OrderItemRepo;
import com.ashtana.backend.Repository.OrderRepo;
import com.ashtana.backend.Repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    // ✅ Create new order item
    public OrderItemResponseDTO createOrderItem(OrderItemRequestDTO dto) {
        // Validate order exists
        Order order = orderRepo.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + dto.getOrderId()));

        // Validate product exists
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));

        // Check if item already exists in this order
        boolean itemExists = order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(dto.getProductId()));

        if (itemExists) {
            throw new RuntimeException("Product already exists in this order. Use update instead.");
        }

        // Create and save order item
        OrderItem orderItem = toEntity(dto, order);
        OrderItem savedItem = orderItemRepo.save(orderItem);

        // Update order totals
        order.calculateTotals();
        orderRepo.save(order);

        return toDto(savedItem);
    }

    // ✅ Update order item quantity
    public OrderItemResponseDTO updateOrderItemQuantity(Long itemId, Integer newQuantity) {
        if (newQuantity == null || newQuantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        OrderItem orderItem = orderItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + itemId));

        orderItem.updateQuantity(newQuantity);
        OrderItem updatedItem = orderItemRepo.save(orderItem);

        // Update order totals
        Order order = updatedItem.getOrder();
        order.calculateTotals();
        orderRepo.save(order);

        return toDto(updatedItem);
    }

    // ✅ Get order item by ID
    public OrderItemResponseDTO getOrderItemById(Long id) {
        OrderItem orderItem = orderItemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + id));
        return toDto(orderItem);
    }

    // ✅ Get all items for an order
    public List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId) {
        // Verify order exists
        if (!orderRepo.existsById(orderId)) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        return orderItemRepo.findByOrderId(orderId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Delete order item
    @Transactional
    public void deleteOrderItem(Long itemId) {
        OrderItem orderItem = orderItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + itemId));

        Order order = orderItem.getOrder();

        orderItemRepo.delete(orderItem);

        // Update order totals
        order.calculateTotals();
        orderRepo.save(order);
    }

    // ✅ Convert Entity → DTO
    public OrderItemResponseDTO toDto(OrderItem item) {
        if (item == null) return null;

        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setPricePerItem(item.getPrice());
        dto.setTotalPrice(item.getTotalPrice());

        // Add product image if available
        if (item.getProductImage() != null) {
            dto.setProductImage(item.getProductImage());
        }

        return dto;
    }

    // ✅ Convert DTO → Entity
    private OrderItem toEntity(OrderItemRequestDTO dto, Order order) {
        if (dto == null || order == null) {
            throw new IllegalArgumentException("DTO and Order cannot be null");
        }

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPrice(product.getPrice()); // Use current product price
        orderItem.setProductName(product.getName());

        // Calculate total price
        orderItem.calculateTotalPrice();

        return orderItem;
    }

    // ✅ Bulk operations
    @Transactional
    public List<OrderItemResponseDTO> createOrderItems(List<OrderItemRequestDTO> dtos, Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        List<OrderItem> orderItems = dtos.stream()
                .map(dto -> toEntity(dto, order))
                .collect(Collectors.toList());

        List<OrderItem> savedItems = orderItemRepo.saveAll(orderItems);

        // Update order totals
        order.calculateTotals();
        orderRepo.save(order);

        return savedItems.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}