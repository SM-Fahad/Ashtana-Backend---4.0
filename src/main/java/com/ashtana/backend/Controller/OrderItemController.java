package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.OrderItemRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.OrderItemResponseDTO;
import com.ashtana.backend.Service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<OrderItemResponseDTO> createOrderItem(@RequestBody OrderItemRequestDTO dto) {
        return ResponseEntity.ok(orderItemService.createOrderItem(dto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<OrderItemResponseDTO>> createOrderItems(
            @RequestBody List<OrderItemRequestDTO> dtos,
            @RequestParam Long orderId) {
        return ResponseEntity.ok(orderItemService.createOrderItems(dtos, orderId));
    }

    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<OrderItemResponseDTO> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(orderItemService.updateOrderItemQuantity(itemId, quantity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponseDTO> getOrderItemById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemResponseDTO>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok().build();
    }
}