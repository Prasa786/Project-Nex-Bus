<<<<<<< HEAD
package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.AmenitiesDTO;
import com.nexbus.nexbus_backend.dto.BusDTO;
import com.nexbus.nexbus_backend.dto.DriverDTO;
import com.nexbus.nexbus_backend.dto.MaintenanceRecordDTO;
import com.nexbus.nexbus_backend.dto.ScheduleDTO;
import com.nexbus.nexbus_backend.dto.SeatDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.exception.SeatInUseException;
import com.nexbus.nexbus_backend.model.Amenity;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.BusOperator;
import com.nexbus.nexbus_backend.model.Driver;
import com.nexbus.nexbus_backend.model.MaintenanceRecord;
import com.nexbus.nexbus_backend.model.Route;
import com.nexbus.nexbus_backend.model.Schedule;
import com.nexbus.nexbus_backend.model.Seat;
import com.nexbus.nexbus_backend.repository.AmenityRepository;
import com.nexbus.nexbus_backend.repository.BusOperatorRepository;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.DriverRepository;
import com.nexbus.nexbus_backend.repository.MaintenanceRecordRepository;
import com.nexbus.nexbus_backend.repository.RouteRepository;
import com.nexbus.nexbus_backend.repository.ScheduleRepository;
import com.nexbus.nexbus_backend.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {

    private static final Logger logger = LoggerFactory.getLogger(BusService.class);

    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final AmenityRepository amenityRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final ScheduleRepository scheduleRepository;
    private final BusOperatorRepository busOperatorRepository;

    public BusService(BusRepository busRepository, SeatRepository seatRepository, AmenityRepository amenityRepository,
                      DriverRepository driverRepository, RouteRepository routeRepository,
                      MaintenanceRecordRepository maintenanceRecordRepository, ScheduleRepository scheduleRepository,
                      BusOperatorRepository busOperatorRepository) {
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
        this.amenityRepository = amenityRepository;
        this.driverRepository = driverRepository;
        this.routeRepository = routeRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.scheduleRepository = scheduleRepository;
        this.busOperatorRepository = busOperatorRepository;
    }

    public List<BusDTO> findAll() {
        logger.debug("Fetching all buses");
        List<BusDTO> buses = busRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} buses", buses.size());
        return buses;
    }

    public List<BusDTO> findByOperatorId(Integer operatorId) {
        logger.debug("Fetching buses for operator ID: {}", operatorId);
        BusOperator operator = busOperatorRepository.findById(operatorId)
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", operatorId);
                return new ResourceNotFoundException("Operator", "ID", operatorId);
            });
        List<BusDTO> buses = busRepository.findByOperator(operator).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} buses for operator ID: {}", buses.size(), operatorId);
        return buses;
    }

    public BusDTO findById(Integer id) {
        logger.debug("Fetching bus with ID: {}", id);
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", id);
                return new ResourceNotFoundException("Bus", "ID", id);
            });
        logger.info("Found bus with ID: {}", id);
        return mapToDTO(bus);
    }

    public List<SeatDTO> findSeatsByBusId(Integer id) {
        logger.debug("Fetching seats for bus ID: {}", id);
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", id);
                return new ResourceNotFoundException("Bus", "ID", id);
            });
        List<SeatDTO> seats = seatRepository.findByBus(bus).stream()
            .map(this::mapSeatToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} seats for bus ID: {}", seats.size(), id);
        return seats;
    }

    public List<MaintenanceRecordDTO> findMaintenanceRecordsByBusId(Integer busId) {
        logger.debug("Fetching maintenance records for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<MaintenanceRecordDTO> records = maintenanceRecordRepository.findByBus(bus).stream()
            .map(this::mapMaintenanceRecordToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} maintenance records for bus ID: {}", records.size(), busId);
        return records;
    }

    public List<ScheduleDTO> findSchedulesByBusId(Integer busId) {
        logger.debug("Fetching schedules for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<ScheduleDTO> schedules = scheduleRepository.findByBus(bus).stream()
            .map(this::mapScheduleToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} schedules for bus ID: {}", schedules.size(), busId);
        return schedules;
    }

    @Transactional
    public BusDTO save(BusDTO busDTO) {
        logger.debug("Saving new bus for operator ID: {}", busDTO.getOperatorId());
        BusOperator operator = busOperatorRepository.findById(busDTO.getOperatorId())
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", busDTO.getOperatorId());
                return new ResourceNotFoundException("Operator", "ID", busDTO.getOperatorId());
            });

        Bus bus = new Bus();
        bus.setOperator(operator);
        if (busDTO.getRouteId() != null) {
            Route route = routeRepository.findById(busDTO.getRouteId())
                .orElseThrow(() -> {
                    logger.error("Route not found with ID: {}", busDTO.getRouteId());
                    return new ResourceNotFoundException("Route", "ID", busDTO.getRouteId());
                });
            bus.setRoute(route);
        }
        bus.setBusNumber(busDTO.getBusNumber());
        bus.setTotalSeats(busDTO.getTotalSeats());
        bus = busRepository.save(bus);

        // Create seats for the bus
        for (int i = 1; i <= busDTO.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setBus(bus);
            seat.setSeatNumber("S" + i);
            seat.setAvailable(true);
            seatRepository.save(seat);
        }

        logger.info("Saved bus with ID: {}", bus.getBusId());
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO update(Integer id, BusDTO busDTO) {
        logger.debug("Updating bus with ID: {}", id);
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", id);
                return new ResourceNotFoundException("Bus", "ID", id);
            });

        BusOperator operator = busOperatorRepository.findById(busDTO.getOperatorId())
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", busDTO.getOperatorId());
                return new ResourceNotFoundException("Operator", "ID", busDTO.getOperatorId());
            });
        bus.setOperator(operator);

        if (busDTO.getRouteId() != null) {
            Route route = routeRepository.findById(busDTO.getRouteId())
                .orElseThrow(() -> {
                    logger.error("Route not found with ID: {}", busDTO.getRouteId());
                    return new ResourceNotFoundException("Route", "ID", busDTO.getRouteId());
                });
            bus.setRoute(route);
        } else {
            bus.setRoute(null);
        }
        bus.setBusNumber(busDTO.getBusNumber());

        // Update seats if totalSeats changes
        if (!bus.getTotalSeats().equals(busDTO.getTotalSeats())) {
            List<Seat> existingSeats = seatRepository.findByBus(bus);
            for (Seat seat : existingSeats) {
                if (seat.getBooking() != null) {
                    logger.error("Cannot update seats for bus ID: {} because some seats are booked", id);
                    throw new SeatInUseException("Cannot update seats because some seats are booked");
                }
            }
            seatRepository.deleteAll(existingSeats);

            for (int i = 1; i <= busDTO.getTotalSeats(); i++) {
                Seat seat = new Seat();
                seat.setBus(bus);
                seat.setSeatNumber("S" + i);
                seat.setAvailable(true);
                seatRepository.save(seat);
            }
            bus.setTotalSeats(busDTO.getTotalSeats());
        }

        bus = busRepository.save(bus);
        logger.info("Updated bus with ID: {}", id);
        return mapToDTO(bus);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting bus with ID: {}", id);
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", id);
                return new ResourceNotFoundException("Bus", "ID", id);
            });

        List<Seat> seats = seatRepository.findByBus(bus);
        for (Seat seat : seats) {
            if (seat.getBooking() != null) {
                logger.error("Cannot delete bus ID: {} because some seats are booked", id);
                throw new SeatInUseException("Cannot delete bus because some seats are booked");
            }
        }
        seatRepository.deleteAll(seats);
        busRepository.delete(bus);
        logger.info("Deleted bus with ID: {}", id);
    }

    @Transactional
    public BusDTO addAmenityToBus(Integer busId, Integer amenityId) {
        logger.debug("Adding amenity ID: {} to bus ID: {}", amenityId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        Amenity amenity = amenityRepository.findById(amenityId)
            .orElseThrow(() -> {
                logger.error("Amenity not found with ID: {}", amenityId);
                return new ResourceNotFoundException("Amenity", "ID", amenityId);
            });

        bus.getAmenities().add(amenity);
        bus = busRepository.save(bus);
        logger.info("Added amenity ID: {} to bus ID: {}", amenityId, busId);
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO removeAmenityFromBus(Integer busId, Integer amenityId) {
        logger.debug("Removing amenity ID: {} from bus ID: {}", amenityId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        Amenity amenity = amenityRepository.findById(amenityId)
            .orElseThrow(() -> {
                logger.error("Amenity not found with ID: {}", amenityId);
                return new ResourceNotFoundException("Amenity", "ID", amenityId);
            });

        bus.getAmenities().remove(amenity);
        bus = busRepository.save(bus);
        logger.info("Removed amenity ID: {} from bus ID: {}", amenityId, busId);
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO assignDriverToBus(Integer busId, Integer driverId) {
        logger.debug("Assigning driver ID: {} to bus ID: {}", driverId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> {
                logger.error("Driver not found with ID: {}", driverId);
                return new ResourceNotFoundException("Driver", "ID", driverId);
            });

        bus.setDriver(driver);
        bus = busRepository.save(bus);
        logger.info("Assigned driver ID: {} to bus ID: {}", driverId, busId);
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO assignRouteToBus(Integer busId, Integer routeId) {
        logger.debug("Assigning route ID: {} to bus ID: {}", routeId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> {
                logger.error("Route not found with ID: {}", routeId);
                return new ResourceNotFoundException("Route", "ID", routeId);
            });

        bus.setRoute(route);
        bus = busRepository.save(bus);
        logger.info("Assigned route ID: {} to bus ID: {}", routeId, busId);
        return mapToDTO(bus);
    }

    @Transactional
    public MaintenanceRecordDTO addMaintenanceRecord(Integer busId, MaintenanceRecordDTO recordDTO) {
        logger.debug("Adding maintenance record for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });

        MaintenanceRecord record = new MaintenanceRecord();
        record.setBus(bus);
        record.setMaintenanceDate(recordDTO.getMaintenanceDate());
        record.setDescription(recordDTO.getDescription());
        record.setCost(recordDTO.getCost());
        record = maintenanceRecordRepository.save(record);
        logger.info("Added maintenance record ID: {} for bus ID: {}", record.getMaintenanceId(), busId);
        return mapMaintenanceRecordToDTO(record);
    }

    public boolean isBusOwner(Integer busId, Integer userId) {
        logger.debug("Checking if user ID: {} owns bus ID: {}", userId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        boolean isOwner = bus.getOperator().getOperatorId().equals(userId);
        logger.info("User ID: {} owns bus ID: {} - {}", userId, busId, isOwner);
        return isOwner;
    }

    private BusDTO mapToDTO(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setBusId(bus.getBusId());
        dto.setOperatorId(bus.getOperator().getOperatorId());
        dto.setRouteId(bus.getRoute() != null ? bus.getRoute().getRouteId() : null);
        dto.setBusNumber(bus.getBusNumber());
        dto.setTotalSeats(bus.getTotalSeats());
        dto.setAmenities(bus.getAmenities().stream()
            .map(this::mapAmenityToDTO)
            .collect(Collectors.toList()));
        dto.setMaintenanceRecords(bus.getMaintenanceRecords().stream()
            .map(this::mapMaintenanceRecordToDTO)
            .collect(Collectors.toList()));
        dto.setDriver(bus.getDriver() != null ? mapDriverToDTO(bus.getDriver()) : null);
        dto.setSchedules(bus.getSchedules().stream()
            .map(this::mapScheduleToDTO)
            .collect(Collectors.toList()));
        dto.setCreatedAt(bus.getCreatedAt());
        dto.setUpdatedAt(bus.getUpdatedAt());
        return dto;
    }

    private SeatDTO mapSeatToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setBusId(seat.getBus().getBusId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setIsAvailable(seat.isAvailable());
        return dto;
    }

    private AmenitiesDTO mapAmenityToDTO(Amenity amenity) {
        AmenitiesDTO dto = new AmenitiesDTO();
        dto.setAmenityId(amenity.getAmenityId());
        dto.setAmenityName(amenity.getAmenityName());
        dto.setDescription(amenity.getDescription());
        dto.setCreatedAt(amenity.getCreatedAt());
        dto.setUpdatedAt(amenity.getUpdatedAt());
        return dto;
    }

    private MaintenanceRecordDTO mapMaintenanceRecordToDTO(MaintenanceRecord record) {
        MaintenanceRecordDTO dto = new MaintenanceRecordDTO();
        dto.setMaintenanceId(record.getMaintenanceId());
        dto.setBusId(record.getBus().getBusId());
        dto.setMaintenanceDate(record.getMaintenanceDate());
        dto.setDescription(record.getDescription());
        dto.setCost(record.getCost());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }

    private DriverDTO mapDriverToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setDriverId(driver.getDriverId());
        dto.setDriverName(driver.getDriverName());
        dto.setContactNumber(driver.getContactNumber());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setUpdatedAt(driver.getUpdatedAt());
        return dto;
    }

    private ScheduleDTO mapScheduleToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setBusId(schedule.getBus().getBusId());
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());
        return dto;
    }
    
    
=======
package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.AmenitiesDTO;
import com.nexbus.nexbus_backend.dto.BusDTO;
import com.nexbus.nexbus_backend.dto.DriverDTO;
import com.nexbus.nexbus_backend.dto.MaintenanceRecordDTO;
import com.nexbus.nexbus_backend.dto.ScheduleDTO;
import com.nexbus.nexbus_backend.dto.SeatDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.exception.SeatInUseException;
import com.nexbus.nexbus_backend.model.Amenity;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.BusOperator;
import com.nexbus.nexbus_backend.model.Driver;
import com.nexbus.nexbus_backend.model.MaintenanceRecord;
import com.nexbus.nexbus_backend.model.Route;
import com.nexbus.nexbus_backend.model.Schedule;
import com.nexbus.nexbus_backend.model.Seat;
import com.nexbus.nexbus_backend.repository.AmenityRepository;
import com.nexbus.nexbus_backend.repository.BusOperatorRepository;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.DriverRepository;
import com.nexbus.nexbus_backend.repository.MaintenanceRecordRepository;
import com.nexbus.nexbus_backend.repository.RouteRepository;
import com.nexbus.nexbus_backend.repository.ScheduleRepository;
import com.nexbus.nexbus_backend.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {

    private static final Logger logger = LoggerFactory.getLogger(BusService.class);

    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final AmenityRepository amenityRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final ScheduleRepository scheduleRepository;
    private final BusOperatorRepository busOperatorRepository;

    public BusService(BusRepository busRepository, SeatRepository seatRepository, AmenityRepository amenityRepository,
                      DriverRepository driverRepository, RouteRepository routeRepository,
                      MaintenanceRecordRepository maintenanceRecordRepository, ScheduleRepository scheduleRepository,
                      BusOperatorRepository busOperatorRepository) {
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
        this.amenityRepository = amenityRepository;
        this.driverRepository = driverRepository;
        this.routeRepository = routeRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.scheduleRepository = scheduleRepository;
        this.busOperatorRepository = busOperatorRepository;
    }

    public List<BusDTO> findAll() {
        logger.debug("Fetching all buses");
        List<BusDTO> buses = busRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} buses", buses.size());
        return buses;
    }

    public List<BusDTO> findByOperatorId(Integer operatorId) {
        logger.debug("Fetching buses for operator ID: {}", operatorId);
        BusOperator operator = busOperatorRepository.findById(operatorId)
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", operatorId);
                return new ResourceNotFoundException("Operator", "ID", operatorId);
            });
        List<BusDTO> buses = busRepository.findByOperator(operator).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} buses for operator ID: {}", buses.size(), operatorId);
        return buses;
    }

    public BusDTO findById(Integer id) {
        logger.debug("Fetching bus with ID: {}", id);
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", id);
                return new ResourceNotFoundException("Bus", "ID", id);
            });
        logger.info("Found bus with ID: {}", id);
        return mapToDTO(bus);
    }

    public List<SeatDTO> findSeatsByBusId(Integer id) {
        logger.debug("Fetching seats for bus ID: {}", id);
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", id);
                return new ResourceNotFoundException("Bus", "ID", id);
            });
        List<SeatDTO> seats = seatRepository.findByBus(bus).stream()
            .map(this::mapSeatToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} seats for bus ID: {}", seats.size(), id);
        return seats;
    }

    public List<MaintenanceRecordDTO> findMaintenanceRecordsByBusId(Integer busId) {
        logger.debug("Fetching maintenance records for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<MaintenanceRecordDTO> records = maintenanceRecordRepository.findByBus(bus).stream()
            .map(this::mapMaintenanceRecordToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} maintenance records for bus ID: {}", records.size(), busId);
        return records;
    }

    public List<ScheduleDTO> findSchedulesByBusId(Integer busId) {
        logger.debug("Fetching schedules for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<ScheduleDTO> schedules = scheduleRepository.findByBus(bus).stream()
            .map(this::mapScheduleToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} schedules for bus ID: {}", schedules.size(), busId);
        return schedules;
    }

    @Transactional
    public BusDTO save(BusDTO busDTO) {
        logger.debug("Saving new bus for operator ID: {}", busDTO.getOperatorId());
        BusOperator operator = busOperatorRepository.findById(busDTO.getOperatorId())
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", busDTO.getOperatorId());
                return new ResourceNotFoundException("Operator", "ID", busDTO.getOperatorId());
            });

        Bus bus = new Bus();
        bus.setOperator(operator);
        if (busDTO.getRouteId() != null) {
            Route route = routeRepository.findById(busDTO.getRouteId())
                .orElseThrow(() -> {
                    logger.error("Route not found with ID: {}", busDTO.getRouteId());
                    return new ResourceNotFoundException("Route", "ID", busDTO.getRouteId());
                });
            bus.setRoute(route);
        }
        bus.setBusNumber(busDTO.getBusNumber());
        bus.setTotalSeats(busDTO.getTotalSeats());
        bus = busRepository.save(bus);

        // Create seats for the bus
        for (int i = 1; i <= busDTO.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setBus(bus);
            seat.setSeatNumber("S" + i);
            seat.setAvailable(true);
            seatRepository.save(seat);
        }

        logger.info("Saved bus with ID: {}", bus.getBusId());
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO update(Integer id, BusDTO busDTO) {
        logger.debug("Updating bus with ID: {}", id);
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", id);
                return new ResourceNotFoundException("Bus", "ID", id);
            });

        BusOperator operator = busOperatorRepository.findById(busDTO.getOperatorId())
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", busDTO.getOperatorId());
                return new ResourceNotFoundException("Operator", "ID", busDTO.getOperatorId());
            });
        bus.setOperator(operator);

        if (busDTO.getRouteId() != null) {
            Route route = routeRepository.findById(busDTO.getRouteId())
                .orElseThrow(() -> {
                    logger.error("Route not found with ID: {}", busDTO.getRouteId());
                    return new ResourceNotFoundException("Route", "ID", busDTO.getRouteId());
                });
            bus.setRoute(route);
        } else {
            bus.setRoute(null);
        }
        bus.setBusNumber(busDTO.getBusNumber());

        // Update seats if totalSeats changes
        if (!bus.getTotalSeats().equals(busDTO.getTotalSeats())) {
            List<Seat> existingSeats = seatRepository.findByBus(bus);
            for (Seat seat : existingSeats) {
                if (seat.getBooking() != null) {
                    logger.error("Cannot update seats for bus ID: {} because some seats are booked", id);
                    throw new SeatInUseException("Cannot update seats because some seats are booked");
                }
            }
            seatRepository.deleteAll(existingSeats);

            for (int i = 1; i <= busDTO.getTotalSeats(); i++) {
                Seat seat = new Seat();
                seat.setBus(bus);
                seat.setSeatNumber("S" + i);
                seat.setAvailable(true);
                seatRepository.save(seat);
            }
            bus.setTotalSeats(busDTO.getTotalSeats());
        }

        bus = busRepository.save(bus);
        logger.info("Updated bus with ID: {}", id);
        return mapToDTO(bus);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting bus with ID: {}", id);
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", id);
                return new ResourceNotFoundException("Bus", "ID", id);
            });

        List<Seat> seats = seatRepository.findByBus(bus);
        for (Seat seat : seats) {
            if (seat.getBooking() != null) {
                logger.error("Cannot delete bus ID: {} because some seats are booked", id);
                throw new SeatInUseException("Cannot delete bus because some seats are booked");
            }
        }
        seatRepository.deleteAll(seats);
        busRepository.delete(bus);
        logger.info("Deleted bus with ID: {}", id);
    }

    @Transactional
    public BusDTO addAmenityToBus(Integer busId, Integer amenityId) {
        logger.debug("Adding amenity ID: {} to bus ID: {}", amenityId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        Amenity amenity = amenityRepository.findById(amenityId)
            .orElseThrow(() -> {
                logger.error("Amenity not found with ID: {}", amenityId);
                return new ResourceNotFoundException("Amenity", "ID", amenityId);
            });

        bus.getAmenities().add(amenity);
        bus = busRepository.save(bus);
        logger.info("Added amenity ID: {} to bus ID: {}", amenityId, busId);
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO removeAmenityFromBus(Integer busId, Integer amenityId) {
        logger.debug("Removing amenity ID: {} from bus ID: {}", amenityId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        Amenity amenity = amenityRepository.findById(amenityId)
            .orElseThrow(() -> {
                logger.error("Amenity not found with ID: {}", amenityId);
                return new ResourceNotFoundException("Amenity", "ID", amenityId);
            });

        bus.getAmenities().remove(amenity);
        bus = busRepository.save(bus);
        logger.info("Removed amenity ID: {} from bus ID: {}", amenityId, busId);
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO assignDriverToBus(Integer busId, Integer driverId) {
        logger.debug("Assigning driver ID: {} to bus ID: {}", driverId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> {
                logger.error("Driver not found with ID: {}", driverId);
                return new ResourceNotFoundException("Driver", "ID", driverId);
            });

        bus.setDriver(driver);
        bus = busRepository.save(bus);
        logger.info("Assigned driver ID: {} to bus ID: {}", driverId, busId);
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO assignRouteToBus(Integer busId, Integer routeId) {
        logger.debug("Assigning route ID: {} to bus ID: {}", routeId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> {
                logger.error("Route not found with ID: {}", routeId);
                return new ResourceNotFoundException("Route", "ID", routeId);
            });

        bus.setRoute(route);
        bus = busRepository.save(bus);
        logger.info("Assigned route ID: {} to bus ID: {}", routeId, busId);
        return mapToDTO(bus);
    }

    @Transactional
    public MaintenanceRecordDTO addMaintenanceRecord(Integer busId, MaintenanceRecordDTO recordDTO) {
        logger.debug("Adding maintenance record for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });

        MaintenanceRecord record = new MaintenanceRecord();
        record.setBus(bus);
        record.setMaintenanceDate(recordDTO.getMaintenanceDate());
        record.setDescription(recordDTO.getDescription());
        record.setCost(recordDTO.getCost());
        record = maintenanceRecordRepository.save(record);
        logger.info("Added maintenance record ID: {} for bus ID: {}", record.getMaintenanceId(), busId);
        return mapMaintenanceRecordToDTO(record);
    }

    public boolean isBusOwner(Integer busId, Integer userId) {
        logger.debug("Checking if user ID: {} owns bus ID: {}", userId, busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        boolean isOwner = bus.getOperator().getOperatorId().equals(userId);
        logger.info("User ID: {} owns bus ID: {} - {}", userId, busId, isOwner);
        return isOwner;
    }

    private BusDTO mapToDTO(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setBusId(bus.getBusId());
        dto.setOperatorId(bus.getOperator().getOperatorId());
        dto.setRouteId(bus.getRoute() != null ? bus.getRoute().getRouteId() : null);
        dto.setBusNumber(bus.getBusNumber());
        dto.setTotalSeats(bus.getTotalSeats());
        dto.setAmenities(bus.getAmenities().stream()
            .map(this::mapAmenityToDTO)
            .collect(Collectors.toList()));
        dto.setMaintenanceRecords(bus.getMaintenanceRecords().stream()
            .map(this::mapMaintenanceRecordToDTO)
            .collect(Collectors.toList()));
        dto.setDriver(bus.getDriver() != null ? mapDriverToDTO(bus.getDriver()) : null);
        dto.setSchedules(bus.getSchedules().stream()
            .map(this::mapScheduleToDTO)
            .collect(Collectors.toList()));
        dto.setCreatedAt(bus.getCreatedAt());
        dto.setUpdatedAt(bus.getUpdatedAt());
        return dto;
    }

    private SeatDTO mapSeatToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setBusId(seat.getBus().getBusId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setIsAvailable(seat.isAvailable());
        return dto;
    }

    private AmenitiesDTO mapAmenityToDTO(Amenity amenity) {
        AmenitiesDTO dto = new AmenitiesDTO();
        dto.setAmenityId(amenity.getAmenityId());
        dto.setAmenityName(amenity.getAmenityName());
        dto.setDescription(amenity.getDescription());
        dto.setCreatedAt(amenity.getCreatedAt());
        dto.setUpdatedAt(amenity.getUpdatedAt());
        return dto;
    }

    private MaintenanceRecordDTO mapMaintenanceRecordToDTO(MaintenanceRecord record) {
        MaintenanceRecordDTO dto = new MaintenanceRecordDTO();
        dto.setMaintenanceId(record.getMaintenanceId());
        dto.setBusId(record.getBus().getBusId());
        dto.setMaintenanceDate(record.getMaintenanceDate());
        dto.setDescription(record.getDescription());
        dto.setCost(record.getCost());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }

    private DriverDTO mapDriverToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setDriverId(driver.getDriverId());
        dto.setDriverName(driver.getDriverName());
        dto.setContactNumber(driver.getContactNumber());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setUpdatedAt(driver.getUpdatedAt());
        return dto;
    }

    private ScheduleDTO mapScheduleToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setBusId(schedule.getBus().getBusId());
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());
        return dto;
    }
    
    
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}