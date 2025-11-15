package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.MyBagItemRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.MyBagItemResponseDTO;
import com.ashtana.backend.Service.MyBagItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bag_items")
@CrossOrigin(origins = "http://localhost:4200")
public class MyBagItemController {

    private final MyBagItemService myBagItemService;

    public MyBagItemController(MyBagItemService myBagItemService) {
        this.myBagItemService = myBagItemService;
    }

    @PostMapping
    public ResponseEntity<MyBagItemResponseDTO> addItemToBag(@RequestBody MyBagItemRequestDTO dto) {
        return ResponseEntity.ok(myBagItemService.addItemToBag(dto));
    }

    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<MyBagItemResponseDTO> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(myBagItemService.updateItemQuantity(itemId, quantity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyBagItemResponseDTO> getCartItemById(@PathVariable Long id) {
        return ResponseEntity.ok(myBagItemService.getById(id));
    }

    @GetMapping("/bag/{bagId}")
    public ResponseEntity<List<MyBagItemResponseDTO>> getItemsByBagId(@PathVariable Long bagId) {
        return ResponseEntity.ok(myBagItemService.getItemsByBagId(bagId));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<MyBagItemResponseDTO> deleteCartItem(@PathVariable Long id) {
//        return ResponseEntity.ok(myBagItemService.deleteItem(id));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MyBagItemResponseDTO> deleteCartItem(@PathVariable Long id) {
        return ResponseEntity.ok(myBagItemService.deleteItemNative(id));
    }
}