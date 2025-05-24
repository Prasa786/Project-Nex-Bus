<<<<<<< HEAD
package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.BusOperator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusOperatorRepository extends JpaRepository<BusOperator, Integer> {
    BusOperator findByEmail(String email);
    
=======
package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.BusOperator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusOperatorRepository extends JpaRepository<BusOperator, Integer> {
    Optional<BusOperator> findByEmail(String email);
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}