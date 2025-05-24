<<<<<<< HEAD
package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(Role.RoleName roleName);
}
=======
package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(Role.RoleName roleName);
}
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
