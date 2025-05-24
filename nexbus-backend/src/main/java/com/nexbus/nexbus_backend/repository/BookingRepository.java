package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Booking;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Schedule;
import com.nexbus.nexbus_backend.model.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Add method to count bookings by seat ID
    long countBySeatSeatId(Integer seatId);
    List<Booking> findByBus(Bus bus);
    List<Booking> findByUser(User user);

    // Optional: Add method from prior messages for consistency
    List<Booking> findByUserUserId(Integer userId);
    List<Booking> findBySchedule(Schedule schedule);


    List<Booking> findByUser_UserId(Integer userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CONFIRMED'")
    Long countConfirmedBookings();

    @Query("SELECT SUM(b.fare) FROM Booking b WHERE b.status = 'CONFIRMED'")
    Double sumConfirmedFares();

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt <= :endDate")
    Long countBookingsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}