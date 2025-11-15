package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.MyBagItemRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.MyBagItemResponseDTO;
import com.ashtana.backend.Entity.MyBag;
import com.ashtana.backend.Entity.MyBagItems;
import com.ashtana.backend.Entity.Product;
import com.ashtana.backend.Repository.MyBagItemsRepo;
import com.ashtana.backend.Repository.MyBagRepo;
import com.ashtana.backend.Repository.ProductRepo;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

    public MyBagItemService(MyBagItemsRepo myBagItemRepo, MyBagRepo myBagRepo,
                            ProductRepo productRepo, MyBagService myBagService,
                            EntityManager entityManager) {
        this.myBagItemRepo = myBagItemRepo;
        this.myBagRepo = myBagRepo;
        this.productRepo = productRepo;
        this.myBagService = myBagService;
        this.entityManager = entityManager;
    }

    // ➤ Delete item - COMPLETELY FIXED VERSION
    public MyBagItemResponseDTO deleteItem(Long id) {
        // Get the item with its bag
        MyBagItems item = myBagItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("My Bag Items not found with ID: " + id));

        // Store bag ID and convert to DTO BEFORE any deletion
        Long bagId = item.getMyBag().getId();
        MyBagItemResponseDTO deletedDto = toDto(item);

        // Remove the item from the bag's collection FIRST
        MyBag myBag = item.getMyBag();
        myBag.getItems().removeIf(bagItem -> bagItem.getId().equals(id));

        // Delete the item
        myBagItemRepo.delete(item);

        // Clear the persistence context to avoid detached entity issues
        entityManager.flush();
        entityManager.clear();

        // Recalculate totals using a fresh query
        recalculateBagTotalSafely(bagId);

        return deletedDto;
    }

    // ➤ Safe recalculation method
    private void recalculateBagTotalSafely(Long bagId) {
        // Get a fresh instance of the bag from database
        MyBag freshBag = myBagRepo.findById(bagId)
                .orElseThrow(() -> new EntityNotFoundException("Bag not found with ID: " + bagId));

        // Recalculate and save
        freshBag.recalculateTotal();
        myBagRepo.save(freshBag);
    }

    // ➤ Alternative delete method using native query (if above doesn't work)
    public MyBagItemResponseDTO deleteItemNative(Long id) {
        // Get the item first to return as DTO
        MyBagItems item = myBagItemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("My Bag Items not found with ID: " + id));

        Long bagId = item.getMyBag().getId();
        MyBagItemResponseDTO deletedDto = toDto(item);

        // Use native query to delete without entity state issues
        myBagItemRepo.deleteById(id);

        // Force flush and clear
        entityManager.flush();
        entityManager.clear();

        // Recalculate safely
        recalculateBagTotalSafely(bagId);

        return deletedDto;
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

        // Recalculate bag total safely
        recalculateBagTotalSafely(myBag.getId());

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

        // Recalculate bag total safely
        recalculateBagTotalSafely(item.getMyBag().getId());

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
}