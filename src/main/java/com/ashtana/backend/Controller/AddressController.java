//package com.ashtana.backend.Controller;
//
//
//import com.ashtana.backend.DTO.RequestDTO.AddressRequestDTO;
//import com.ashtana.backend.DTO.ResponseDTO.AddressResponseDTO;
//import com.ashtana.backend.Service.AddressService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/addresses")
//@CrossOrigin(origins = "http://localhost:4200")
//public class AddressController {
//
//    private final AddressService addressService;
//
//    public AddressController(AddressService addressService) {
//        this.addressService = addressService;
//    }
//
//
//    // ➕ Create new address
//    @PostMapping
//    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody AddressRequestDTO dto) {
//        return ResponseEntity.ok(addressService.createAddress(dto));
//    }
//
//    // 🔍 Get address by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long id) {
//        return ResponseEntity.ok(addressService.getAddressById(id));
//    }
//
//    // 📋 Get all addresses
//    @GetMapping
//    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
//        return ResponseEntity.ok(addressService.getAllAddresses());
//    }
//
//    // ✏️ Update address
//    @PutMapping("/{id}")
//    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long id, @RequestBody AddressRequestDTO dto) {
//        return ResponseEntity.ok(addressService.updateAddress(id, dto));
//    }
//
//    // ❌ Delete address
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
//        return ResponseEntity.ok(addressService.deleteAddress(id));
//    }
//}

package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.AddressRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.AddressResponseDTO;
import com.ashtana.backend.Service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "http://localhost:4200")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // ➕ Create new address
    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.createAddress(dto));
    }

    // 🔍 Get address by ID
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    // 📋 Get all addresses
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // 👤 Get addresses by user
    @GetMapping("/user/{userName}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByUser(@PathVariable String userName) {
        return ResponseEntity.ok(addressService.getAddressesByUser(userName));
    }

    // ✏️ Update address
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long id, @RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.updateAddress(id, dto));
    }

    // ❌ Delete address
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.deleteAddress(id));
    }
}