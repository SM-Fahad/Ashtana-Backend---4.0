package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.MyBagRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.MyBagItemResponseDTO;
import com.ashtana.backend.DTO.ResponseDTO.MyBagResponseDTO;
import com.ashtana.backend.Entity.MyBag;
import com.ashtana.backend.Entity.User;
import com.ashtana.backend.Repository.MyBagRepo;
import com.ashtana.backend.Repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MyBagService {

    private final MyBagRepo myBagRepo;
    private final UserRepo userRepo;

    public MyBagService(MyBagRepo myBagRepo, UserRepo userRepo) {
        this.myBagRepo = myBagRepo;
        this.userRepo = userRepo;
    }

    // ➤ Get or Create bag for user
    public MyBag getOrCreateBag(String userName) {
        return myBagRepo.findByUserUserName(userName)
                .orElseGet(() -> {
                    User user = userRepo.findByUserName(userName)
                            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userName));
                    MyBag newBag = new MyBag();
                    newBag.setUser(user);
                    return myBagRepo.save(newBag);
                });
    }

    // ➤ Convert Entity → DTO
    public MyBagResponseDTO toDto(MyBag myBag) {
        MyBagResponseDTO dto = new MyBagResponseDTO();
        dto.setId(myBag.getId());
        dto.setUserName(myBag.getUser().getUserName());
        dto.setTotalItems(myBag.getTotalItems());
        dto.setTotalPrice(myBag.getTotalPrice());

        // Convert items to DTOs
        if (myBag.getItems() != null) {
            dto.setItems(myBag.getItems().stream().map(item -> {
                MyBagItemResponseDTO itemDto = new MyBagItemResponseDTO();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setPricePerItem(item.getProduct().getPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setTotalPrice(item.getTotalPrice());
                return itemDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    // ➤ Create Bag (if doesn't exist)
    public MyBagResponseDTO createBag(MyBagRequestDTO dto) {
        MyBag bag = getOrCreateBag(dto.getUserName());
        return toDto(bag);
    }

    // ➤ Get Bag by user name
    public MyBagResponseDTO getMyBagByUserName(String userName) {
        MyBag myBag = getOrCreateBag(userName);
        return toDto(myBag);
    }

    // ➤ Get Bag by ID
    public MyBagResponseDTO getMyBagById(Long id) {
        MyBag myBag = myBagRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bag not found with ID: " + id));
        return toDto(myBag);
    }

    // ➤ Get all Bags
    public List<MyBagResponseDTO> getAllMyBag() {
        return myBagRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    // ➤ Delete Bag
    public String deleteFromBag(Long id) {
        MyBag myBag = myBagRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("My Bag not found with ID: " + id));
        myBagRepo.delete(myBag);
        return "My Bag deleted successfully.";
    }

    // ➤ Recalculate total price
    public void recalculateTotal(MyBag myBag) {
        myBag.recalculateTotal();
        myBagRepo.save(myBag);
    }
}