package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.MyBagRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.MyBagResponseDTO;
import com.ashtana.backend.Service.MyBagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mybag")
@CrossOrigin(origins = "http://localhost:4200")
public class MyBagController {

    private final MyBagService myBagService;

    public MyBagController(MyBagService myBagService) {
        this.myBagService = myBagService;
    }

    @PostMapping
    public ResponseEntity<MyBagResponseDTO> createCart(@RequestBody MyBagRequestDTO dto) {
        return ResponseEntity.ok(myBagService.createBag(dto));
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<MyBagResponseDTO> getCartByUserName(@PathVariable String userName) {
        return ResponseEntity.ok(myBagService.getMyBagByUserName(userName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyBagResponseDTO> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(myBagService.getMyBagById(id));
    }

    @GetMapping
    public ResponseEntity<List<MyBagResponseDTO>> getAllMyBag() {
        return ResponseEntity.ok(myBagService.getAllMyBag());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFromBag(@PathVariable Long id) {
        return ResponseEntity.ok(myBagService.deleteFromBag(id));
    }
}