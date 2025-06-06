package com.nexbus.frontendNex.service;

import com.nexbus.frontendNex.dto.SupportDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SupportService {
    private final ApiClientService apiService;

    public SupportService(ApiClientService apiService) {
        this.apiService = apiService;
    }

    public List<SupportDTO> getAllTickets() {
        SupportDTO[] tickets = apiService.get("/api/support", SupportDTO[].class);
        return Arrays.asList(tickets);
    }

    public SupportDTO createTicket(SupportDTO supportDTO) {
        return apiService.post("/api/support", supportDTO, SupportDTO.class);
    }

    public SupportDTO updateTicket(Integer id, SupportDTO supportDTO) {
        return apiService.put("/api/support/" + id, supportDTO, SupportDTO.class);
    }

    public void deleteTicket(Integer id) {
        apiService.delete("/api/support/" + id);
    }
}