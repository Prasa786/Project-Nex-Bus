package com.nexbus.frontendNex.service;

import com.nexbus.frontendNex.dto.RouteDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RouteService {
    private final ApiClientService apiService;

    public RouteService(ApiClientService apiService) {
        this.apiService = apiService;
    }

    public List<RouteDTO> getAllRoutes() {
        RouteDTO[] routes = apiService.get("/api/routes", RouteDTO[].class);
        return Arrays.asList(routes);
    }

    public RouteDTO createRoute(RouteDTO routeDTO) {
        return apiService.post("/api/routes", routeDTO, RouteDTO.class);
    }

    public RouteDTO updateRoute(Integer id, RouteDTO routeDTO) {
        return apiService.put("/api/routes/" + id, routeDTO, RouteDTO.class);
    }

    public void deleteRoute(Integer id) {
        apiService.delete("/api/routes/" + id);
    }
}