package com.nexbus.frontendNex.service;

import com.nexbus.frontendNex.dto.PassengerDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PassengerService {
    private final ApiClientService apiService;

    public PassengerService(ApiClientService apiService) {
        this.apiService = apiService;
    }

    public List<PassengerDTO> getAllPassengers() {
        PassengerDTO[] passengers = apiService.get("/api/passengers", PassengerDTO[].class);
        return Arrays.asList(passengers);
    }

    public PassengerDTO createPassenger(PassengerDTO passengerDTO) {
        return apiService.post("/api/users/register", passengerDTO, PassengerDTO.class);
    }

    public PassengerDTO updatePassenger(Integer id, PassengerDTO passengerDTO) {
        return apiService.put("/api/passengers/" + id, passengerDTO, PassengerDTO.class);
    }

    public void deletePassenger(Integer id) {
        apiService.delete("/api/passengers/" + id);
    }
}