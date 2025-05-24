package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(Role.RoleName roleName);
}
