package com.ashtana.backend.Repository;


import com.ashtana.backend.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, String> {
    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleNameIgnoreCase(String roleName);
}