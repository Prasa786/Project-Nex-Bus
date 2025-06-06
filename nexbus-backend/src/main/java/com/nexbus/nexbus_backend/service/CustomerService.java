package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.BookingDTO;
import com.nexbus.nexbus_backend.dto.BusDTO;
import com.nexbus.nexbus_backend.dto.CustomerDTO;
import com.nexbus.nexbus_backend.dto.LoginResponse;
import com.nexbus.nexbus_backend.dto.SeatDTO;

import java.util.List;

public interface CustomerService {
    List<CustomerDTO> findAll();
    CustomerDTO findById(Long id);
    CustomerDTO register(CustomerDTO dto);
    CustomerDTO update(Long id, CustomerDTO dto);
    void delete(Long id);
    LoginResponse login(String email, String password);
    List<BusDTO> findAvailableBuses(String source, String destination, String date);
    List<SeatDTO> findAllSeats(Integer busId);
    List<BookingDTO> findCustomerBookings(Long customerId);
    BookingDTO bookTicket(Long customerId, Integer busId, String seatNumber);
}