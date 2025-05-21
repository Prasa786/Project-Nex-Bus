package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.RouteDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Route;
import com.nexbus.nexbus_backend.repository.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<RouteDTO> findAll() {
        logger.debug("Fetching all routes");
        List<RouteDTO> routes = routeRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} routes", routes.size());
        return routes;
    }

    public RouteDTO findById(Integer id) {
        logger.debug("Fetching route with ID: {}", id);
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Route not found with ID: {}", id);
                return new ResourceNotFoundException("Route", "ID", id);
            });
        logger.info("Found route with ID: {}", id);
        return mapToDTO(route);
    }

    @Transactional
    public RouteDTO save(RouteDTO routeDTO) {
        logger.debug("Saving new route: {}", routeDTO.getRouteName());
        Route route = mapToEntity(routeDTO);
        route = routeRepository.save(route);
        logger.info("Saved route with ID: {}", route.getRouteId());
        return mapToDTO(route);
    }

    @Transactional
    public RouteDTO update(Integer id, RouteDTO routeDTO) {
        logger.debug("Updating route with ID: {}", id);
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Route not found with ID: {}", id);
                return new ResourceNotFoundException("Route", "ID", id);
            });
        route.setRouteName(routeDTO.getRouteName());
        route.setStartLocation(routeDTO.getStartLocation());
        route.setEndLocation(routeDTO.getEndLocation());
        route.setDistance(routeDTO.getDistance());
        route = routeRepository.save(route);
        logger.info("Updated route with ID: {}", id);
        return mapToDTO(route);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting route with ID: {}", id);
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Route not found with ID: {}", id);
                return new ResourceNotFoundException("Route", "ID", id);
            });
        routeRepository.delete(route);
        logger.info("Deleted route with ID: {}", id);
    }

    private RouteDTO mapToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setRouteName(route.getRouteName());
        dto.setStartLocation(route.getStartLocation());
        dto.setEndLocation(route.getEndLocation());
        dto.setDistance(route.getDistance());
        dto.setCreatedAt(route.getCreatedAt());
        dto.setUpdatedAt(route.getUpdatedAt());
        return dto;
    }

    private Route mapToEntity(RouteDTO dto) {
        Route route = new Route();
        route.setRouteId(dto.getRouteId());
        route.setRouteName(dto.getRouteName());
        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());
        return route;
    }
}