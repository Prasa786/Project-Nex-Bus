package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.RouteDTO;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Route;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.RouteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;

    @Autowired
    public RouteService(RouteRepository routeRepository, BusRepository busRepository) {
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
    }

    public List<RouteDTO> findAll() {
        logger.debug("Fetching all routes");
        return routeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RouteDTO> findByOperatorId(Integer userId) {
        logger.debug("Fetching routes for operator userId: {}", userId);
        return routeRepository.findByOperatorUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RouteDTO findById(Integer id) {
        logger.debug("Fetching route with id: {}", id);
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + id));
        return convertToDTO(route);
    }

    public boolean isRouteAccessibleByOperator(Integer routeId, Integer userId) {
        logger.debug("Checking if routeId: {} is accessible by userId: {}", routeId, userId);
        return routeRepository.findByOperatorUserId(userId).stream()
                .anyMatch(route -> route.getRouteId().equals(routeId));
    }

    @Transactional
    public RouteDTO save(RouteDTO routeDTO) {
        logger.debug("Saving new route: {}", routeDTO.getRouteName());
        Route route = convertToEntity(routeDTO);
        Route savedRoute = routeRepository.save(route);
        return convertToDTO(savedRoute);
    }

    @Transactional
    public RouteDTO update(Integer id, RouteDTO routeDTO) {
        logger.debug("Updating route with id: {}", id);
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + id));
        existingRoute.setRouteName(routeDTO.getRouteName());
        existingRoute.setStartLocation(routeDTO.getStartLocation());
        existingRoute.setEndLocation(routeDTO.getEndLocation());
        existingRoute.setDistance(routeDTO.getDistance());
        Route updatedRoute = routeRepository.save(existingRoute);
        return convertToDTO(updatedRoute);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting route with id: {}", id);
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + id));
        List<Bus> buses = busRepository.findByRoute_RouteId(id);
        if (!buses.isEmpty()) {
            throw new IllegalStateException("Cannot delete route assigned to buses");
        }
        routeRepository.delete(route);
    }

    private RouteDTO convertToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setRouteName(route.getRouteName());
        dto.setStartLocation(route.getStartLocation());
        dto.setEndLocation(route.getEndLocation());
        dto.setDistance(route.getDistance());
        return dto;
    }

    private Route convertToEntity(RouteDTO dto) {
        Route route = new Route();
        route.setRouteId(dto.getRouteId());
        route.setRouteName(dto.getRouteName());
        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());
        return route;
    }
}