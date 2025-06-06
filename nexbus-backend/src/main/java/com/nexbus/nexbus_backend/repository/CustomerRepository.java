package com.nexbus.nexbus_backend.repository;



import com.nexbus.nexbus_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByEmail(String email);
}