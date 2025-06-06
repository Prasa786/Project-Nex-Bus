package com.nexbus.frontendNex.service;

import com.nexbus.frontendNex.dto.BusDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BusService {
    private final ApiClientService apiService;

    public BusService(ApiClientService apiService) {
        this.apiService = apiService;
    }

    public List<BusDTO> getAllBuses() {
        BusDTO[] buses = apiService.get("/api/buses", BusDTO[].class);
        return Arrays.asList(buses);
    }

    public BusDTO createBus(BusDTO busDTO) {
        return apiService.post("/api/buses", busDTO, BusDTO.class);
    }

    public BusDTO updateBus(Integer id, BusDTO busDTO) {
        return apiService.put("/api/buses/" + id, busDTO, BusDTO.class);
    }

    public void deleteBus(Integer id) {
        apiService.delete("/api/buses/" + id);
    }
}