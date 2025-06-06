package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.AmenitiesDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Amenity;
import com.nexbus.nexbus_backend.repository.AmenityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityService {

    private static final Logger logger = LoggerFactory.getLogger(AmenityService.class);

    private final AmenityRepository amenityRepository;

    public AmenityService(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    public List<AmenitiesDTO> findAll() {
        logger.debug("Fetching all amenities");
        List<AmenitiesDTO> amenities = amenityRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} amenities", amenities.size());
        return amenities;
    }

    public AmenitiesDTO findById(Integer id) {
        logger.debug("Fetching amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Amenity not found with ID: {}", id);
                return new ResourceNotFoundException("Amenity", "ID", id);
            });
        logger.info("Found amenity with ID: {}", id);
        return mapToDTO(amenity);
    }

    @Transactional
    public AmenitiesDTO save(AmenitiesDTO amenitiesDTO) {
        logger.debug("Saving new amenity: {}", amenitiesDTO.getAmenityName());
        Amenity amenity = new Amenity();
        amenity.setAmenityName(amenitiesDTO.getAmenityName());
        amenity.setDescription(amenitiesDTO.getDescription());
        amenity = amenityRepository.save(amenity);
        logger.info("Saved amenity with ID: {}", amenity.getAmenityId());
        return mapToDTO(amenity);
    }

    @Transactional
    public AmenitiesDTO update(Integer id, AmenitiesDTO amenitiesDTO) {
        logger.debug("Updating amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Amenity not found with ID: {}", id);
                return new ResourceNotFoundException("Amenity", "ID", id);
            });
        amenity.setAmenityName(amenitiesDTO.getAmenityName());
        amenity.setDescription(amenitiesDTO.getDescription());
        amenity = amenityRepository.save(amenity);
        logger.info("Updated amenity with ID: {}", id);
        return mapToDTO(amenity);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Amenity not found with ID: {}", id);
                return new ResourceNotFoundException("Amenity", "ID", id);
            });
        amenityRepository.delete(amenity);
        logger.info("Deleted amenity with ID: {}", id);
    }

    private AmenitiesDTO mapToDTO(Amenity amenity) {
        AmenitiesDTO dto = new AmenitiesDTO();
        dto.setAmenityId(amenity.getAmenityId());
        dto.setAmenityName(amenity.getAmenityName());
        dto.setDescription(amenity.getDescription());
        dto.setCreatedAt(amenity.getCreatedAt());
        dto.setUpdatedAt(amenity.getUpdatedAt());
        return dto;
    }
}