package com.nexbus.frontendNex.service;

import com.nexbus.frontendNex.dto.OperatorDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OperatorService {
    private final ApiClientService apiService;

    public OperatorService(ApiClientService apiService) {
        this.apiService = apiService;
    }

    public List<OperatorDTO> getAllOperators() {
        OperatorDTO[] operators = apiService.get("/bus-operators", OperatorDTO[].class);
        return Arrays.asList(operators);
    }

    public OperatorDTO createOperator(OperatorDTO operatorDTO) {
        return apiService.post("/bus-operators", operatorDTO, OperatorDTO.class);
    }

    public OperatorDTO updateOperator(Integer id, OperatorDTO operatorDTO) {
        return apiService.put("/bus-operators/" + id, operatorDTO, OperatorDTO.class);
    }

    public void deleteOperator(Integer id) {
        apiService.delete("/bus-operators/" + id);
    }
}