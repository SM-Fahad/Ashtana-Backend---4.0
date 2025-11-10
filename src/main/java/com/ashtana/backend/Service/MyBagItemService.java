package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.MyBagItemRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.MyBagItemResponseDTO;
import com.ashtana.backend.Entity.MyBag;
import com.ashtana.backend.Entity.MyBagItems;
import com.ashtana.backend.Entity.Product;
import com.ashtana.backend.Repository.MyBagItemsRepo;
import com.ashtana.backend.Repository.MyBagRepo;
import com.ashtana.backend.Repository.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MyBagItemService {

    private final MyBagItemsRepo myBagItemRepo;
    private final MyBagRepo myBagRepo;
    private final ProductRepo productRepo;
    private final MyBagService myBagService;

    public MyBagItemService(MyBagItemsRepo myBagItemRepo, MyBagRepo myBagRepo, ProductRepo productRepo, MyBagService myBagService) {
        this.myBagItemRepo = myBagItemRepo;
        this.myBagRepo = myBagRepo;
        this.productRepo = productRepo;
        this.myBagService = myBagService;
    }

    // ➤ Add item to bag
    public MyBagItemResponseDTO addItemToBag(MyBagItemRequestDTO dto) {
        // Get or create bag for user
        MyBag myBag = myBagService.getOrCreateBag(dto.getUserName());
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Check if item already exists in bag
        Optional<MyBagItems> existingItem = myBag.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        MyBagItems item;
        if (existingItem.isPresent()) {
            // Update existing item
            item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        } else {
            // Create new item
            item = new MyBagItems();
            item.setMyBag(myBag);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            myBag.getItems().add(item);
        }

        // Calculate total price and save
        item.calculateTotalPrice();
        MyBagItems saved = myBagItemRepo.save(item);
        myBagService.recalculateTotal(myBag);

        return toDto(saved);
    }

    // ➤ Update item quantity
    public MyBagItemResponseDTO updateItemQuantity(Long itemId, Integer quantity) {
        MyBagItems item = myBagItemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        if (quantity <= 0) {
            return deleteItem(itemId);
        }

        item.setQuantity(quantity);
        item.calculateTotalPrice();
        MyBagItems saved = myBagItemRepo.save(item);
        myBagService.recalculateTotal(item.getMyBag());

        return toDto(saved);
    }

    // ➤ Convert Entity → DTO
    public MyBagItemResponseDTO toDto(MyBagItems item) {
        MyBagItemResponseDTO dto = new MyBagItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setPricePerItem(item.getProduct().getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    // ➤ Get item by ID
    public MyBagItemResponseDTO getById(Long id) {
        MyBagItems item = myBagItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));
        return toDto(item);
    }

    // ➤ Get all items for a bag
    public List<MyBagItemResponseDTO> getItemsByBagId(Long bagId) {
        return myBagItemRepo.findByMyBagId(bagId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ➤ Delete item
    public MyBagItemResponseDTO deleteItem(Long id) {
        MyBagItems item = myBagItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("My Bag Items not found"));
        MyBag myBag = item.getMyBag();

        MyBagItemResponseDTO deletedDto = toDto(item);
        myBagItemRepo.delete(item);
        myBagService.recalculateTotal(myBag);

        return deletedDto;
    }
}