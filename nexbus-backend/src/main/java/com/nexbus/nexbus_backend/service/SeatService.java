package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.SeatAvailabilitySummaryDTO;
import com.nexbus.nexbus_backend.dto.SeatDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Seat;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private static final Logger logger = LoggerFactory.getLogger(SeatService.class);

    private final SeatRepository seatRepository;
    private final BusRepository busRepository;

    public SeatService(SeatRepository seatRepository, BusRepository busRepository) {
        this.seatRepository = seatRepository;
        this.busRepository = busRepository;
    }

    public List<SeatDTO> findByBusId(Integer busId) {
        logger.debug("Fetching seats for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<SeatDTO> seats = seatRepository.findByBus(bus).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} seats for bus ID: {}", seats.size(), busId);
        return seats;
    }

    public List<SeatDTO> findAvailableSeatsByBusId(Integer busId) {
        logger.debug("Fetching available seats for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<SeatDTO> seats = seatRepository.findByBusAndAvailableTrue(bus).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} available seats for bus ID: {}", seats.size(), busId);
        return seats;
    }

    public SeatDTO findById(Integer id) {
        logger.debug("Fetching seat with ID: {}", id);
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Seat not found with ID: {}", id);
                return new ResourceNotFoundException("Seat", "ID", id);
            });
        logger.info("Found seat with ID: {}", id);
        return mapToDTO(seat);
    }
    
    public List<SeatDTO> findAll() {
        logger.debug("Fetching all seats");
        List<SeatDTO> seats = seatRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} seats", seats.size());
        return seats;
    }

    private SeatDTO mapToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setBusId(seat.getBus().getBusId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setIsAvailable(seat.isAvailable());
        return dto;
    }
    
    @Transactional
    public SeatDTO save(SeatDTO seatDTO) {
        logger.debug("Saving new seat for bus ID: {}", seatDTO.getBusId());
        Bus bus = busRepository.findById(seatDTO.getBusId())
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", seatDTO.getBusId());
                return new ResourceNotFoundException("Bus", "ID", seatDTO.getBusId());
            });

        Seat seat = new Seat();
        seat.setBus(bus);
        seat.setSeatNumber(seatDTO.getSeatNumber());
        seat.setAvailable(seatDTO.getIsAvailable());
        seat = seatRepository.save(seat);

        logger.info("Saved seat with ID: {}", seat.getSeatId());
        return mapToDTO(seat);
    }

    @Transactional
    public SeatDTO update(Integer id, SeatDTO seatDTO) {
        logger.debug("Updating seat with ID: {}", id);
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Seat not found with ID: {}", id);
                return new ResourceNotFoundException("Seat", "ID", id);
            });

        Bus bus = busRepository.findById(seatDTO.getBusId())
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", seatDTO.getBusId());
                return new ResourceNotFoundException("Bus", "ID", seatDTO.getBusId());
            });

        seat.setBus(bus);
        seat.setSeatNumber(seatDTO.getSeatNumber());
        seat.setAvailable(seatDTO.getIsAvailable());
        seat = seatRepository.save(seat);

        logger.info("Updated seat with ID: {}", id);
        return mapToDTO(seat);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting seat with ID: {}", id);
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Seat not found with ID: {}", id);
                return new ResourceNotFoundException("Seat", "ID", id);
            });

        if (seat.getBooking() != null) {
            logger.error("Seat with ID: {} is currently booked and cannot be deleted", id);
            throw new IllegalStateException("Cannot delete a seat that is currently booked");
        }

        seatRepository.delete(seat);
        logger.info("Deleted seat with ID: {}", id);
    }

    @Transactional
    public void markAsBooked(Integer seatId, Integer userId) {
        logger.debug("Marking seat ID: {} as booked for user ID: {}", seatId, userId);
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> {
                logger.error("Seat not found with ID: {}", seatId);
                return new ResourceNotFoundException("Seat", "ID", seatId);
            });

        if (!seat.isAvailable()) {
            logger.error("Seat with ID: {} is not available for booking", seatId);
            throw new IllegalStateException("Seat is not available for booking");
        }

        seat.setAvailable(false);
        seatRepository.save(seat);
        logger.info("Seat ID: {} marked as booked", seatId);
    }
    
    public SeatAvailabilitySummaryDTO getSeatAvailabilitySummary(Integer busId) {
        logger.debug("Fetching seat availability summary for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });

        Long totalSeats = seatRepository.countByBusBusId(busId);
        Long availableSeats = seatRepository.countByBusBusIdAndAvailableTrue(busId);
        Long bookedSeats = seatRepository.countByBusBusIdAndAvailableFalse(busId);

        SeatAvailabilitySummaryDTO summary = new SeatAvailabilitySummaryDTO();
        summary.setBusId(busId);
        summary.setTotalSeats(totalSeats);
        summary.setAvailableSeats(availableSeats);
        summary.setBookedSeats(bookedSeats);

        logger.info("Seat availability summary for bus ID: {} - Total: {}, Available: {}, Booked: {}", 
            busId, totalSeats, availableSeats, bookedSeats);
        return summary;
    }
    
 

}