package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.AddressRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.AddressResponseDTO;
import com.ashtana.backend.Entity.Address;
import com.ashtana.backend.Entity.User;


import com.ashtana.backend.Repository.AddressRepo;
import com.ashtana.backend.Repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepo addressRepository;
    private final UserRepo userRepository;

    public AddressService(AddressRepo addressRepository, UserRepo userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public AddressResponseDTO createAddress(AddressRequestDTO dto) {
        User user = userRepository.findByUserName(dto.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + dto.getUserName()));

        Address address = new Address();
        address.setRecipientName(dto.getRecipientName());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        address.setType(dto.getType());
        address.setPhone(dto.getPhone());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    public AddressResponseDTO getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
        return convertToDTO(address);
    }

    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AddressResponseDTO> getAddressesByUser(String userName) {
        return addressRepository.findByUserUserName(userName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AddressResponseDTO updateAddress(Long id, AddressRequestDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        address.setRecipientName(dto.getRecipientName());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        address.setType(dto.getType());
        address.setPhone(dto.getPhone());

        Address updatedAddress = addressRepository.save(address);
        return convertToDTO(updatedAddress);
    }

    public String deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        addressRepository.delete(address);
        return "Address deleted successfully";
    }

    private AddressResponseDTO convertToDTO(Address address) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(address.getId());
        dto.setUserName(address.getUser().getUserName());
        dto.setRecipientName(address.getRecipientName());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setPostalCode(address.getPostalCode());
        dto.setType(address.getType());
        dto.setPhone(address.getPhone());
        return dto;
    }
}