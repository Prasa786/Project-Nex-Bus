package com.nexbus.frontendNex.service;

import com.nexbus.frontendNex.dto.BookingDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BookingService {
    private final ApiClientService apiService;

    public BookingService(ApiClientService apiService) {
        this.apiService = apiService;
    }

    public List<BookingDTO> getAllBookings() {
        BookingDTO[] bookings = apiService.get("/bookings", BookingDTO[].class);
        return Arrays.asList(bookings);
    }

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        return apiService.post("/bookings", bookingDTO, BookingDTO.class);
    }

    public void cancelBooking(Integer id) {
        apiService.post("/bookings/" + id + "/cancel", null, Void.class);
    }
}