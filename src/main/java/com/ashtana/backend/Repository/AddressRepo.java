package com.ashtana.backend.Repository;


import com.ashtana.backend.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {
    List<Address> findByUserUserName(String userName);
    Optional<Address> findByIdAndUserUserName(Long id, String userName);
}
