package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Role.RoleName;
import com.nexbus.nexbus_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleId = :roleId")
    long countByRoleId(@Param("roleId") Integer roleId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :roleName")
    Long countByRole_RoleName(RoleName roleName);

    Optional<User> findByUserId(Integer id);
}
