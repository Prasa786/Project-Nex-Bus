package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.BookingDTO;
import com.nexbus.nexbus_backend.dto.BusDTO;
import com.nexbus.nexbus_backend.dto.CustomerDTO;
import com.nexbus.nexbus_backend.dto.LoginResponse;
import com.nexbus.nexbus_backend.dto.SeatDTO;
import com.nexbus.nexbus_backend.exception.CustomerNotFoundException;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Customer;
import com.nexbus.nexbus_backend.model.Route;
import com.nexbus.nexbus_backend.model.Schedule;
import com.nexbus.nexbus_backend.model.Seat;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.CustomerRepository;
import com.nexbus.nexbus_backend.repository.RouteRepository;
import com.nexbus.nexbus_backend.repository.ScheduleRepository;
import com.nexbus.nexbus_backend.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final BookingService bookingService;

    public CustomerServiceImpl(CustomerRepository customerRepository, BusRepository busRepository,
                               RouteRepository routeRepository, ScheduleRepository scheduleRepository,
                               SeatRepository seatRepository, BookingService bookingService) {
        this.customerRepository = customerRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
        this.seatRepository = seatRepository;
        this.bookingService = bookingService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findAll() {
        logger.debug("Fetching all customers");
        List<CustomerDTO> customers = customerRepository.findAll().stream()
            .map(this::mapToCustomerDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} customers", customers.size());
        return customers;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO findById(Long id) {
        logger.debug("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Customer not found with ID: {}", id);
                return new CustomerNotFoundException("Customer not found with ID: " + id);
            });
        logger.info("Found customer with ID: {}", id);
        return mapToCustomerDTO(customer);
    }

    @Override
    @Transactional
    public CustomerDTO register(CustomerDTO dto) {
        logger.debug("Registering new customer with email: {}", dto.getEmail());
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setMobile(dto.getMobile());
        customer.setPassword(dto.getEmail()); // Placeholder; encode in production
        customer = customerRepository.save(customer);
        logger.info("Registered customer with ID: {}", customer.getId());
        return mapToCustomerDTO(customer);
    }

    @Override
    @Transactional
    public CustomerDTO update(Long id, CustomerDTO dto) {
        logger.debug("Updating customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Customer not found with ID: {}", id);
                return new CustomerNotFoundException("Customer not found with ID: " + id);
            });
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setMobile(dto.getMobile());
        customer = customerRepository.save(customer);
        logger.info("Updated customer with ID: {}", id);
        return mapToCustomerDTO(customer);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.debug("Deleting customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            logger.error("Customer not found with ID: {}", id);
            throw new CustomerNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
        logger.info("Deleted customer with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(String email, String password) {
        logger.debug("Attempting login for email: {}", email);
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null || !customer.getPassword().equals(password)) {
            logger.error("Invalid email or password for email: {}", email);
            throw new CustomerNotFoundException("Invalid email or password");
        }
        LoginResponse response = new LoginResponse();
        response.setEmail(email);
        response.setRole("CUSTOMER");
        response.setToken("jwt-token-placeholder"); // Replace with JWT in production
        logger.info("Successful login for customer with email: {}", email);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusDTO> findAvailableBuses(String source, String destination, String date) {
        logger.debug("Fetching available buses from {} to {} on {}", source, destination, date);
        LocalDate travelDate = LocalDate.parse(date);
        List<Route> routes = routeRepository.findByStartLocationAndEndLocation(source, destination);
        if (routes.isEmpty()) {
            logger.info("No routes found from {} to {}", source, destination);
            return List.of();
        }

        List<BusDTO> busDTOs = routes.stream()
            .flatMap(route -> busRepository.findByRoute_RouteId(route.getRouteId()).stream()
                .flatMap(bus -> scheduleRepository.findByBusBusId(bus.getBusId()).stream()
                    .filter(schedule -> schedule.getDepartureTime().toLocalDate().equals(travelDate))
                    .map(schedule -> {
                        BusDTO dto = new BusDTO();
                        dto.setBusId(bus.getBusId());
                        dto.setBusNumber(bus.getBusNumber());
                        dto.setOperatorId(bus.getOperator().getOperatorId());
                        dto.setRouteId(route.getRouteId());
                        dto.setTotalSeats(bus.getTotalSeats());
                        return dto;
                    })))
            .collect(Collectors.toList());
        logger.info("Retrieved {} available buses", busDTOs.size());
        return busDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatDTO> findAllSeats(Integer busId) {
        logger.debug("Fetching all seats for bus ID: {}", busId);
        busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<Seat> seats = seatRepository.findByBusBusId(busId);
        List<SeatDTO> seatDTOs = seats.stream()
            .map(seat -> {
                SeatDTO dto = new SeatDTO();
                dto.setSeatId(seat.getSeatId());
                dto.setBusId(busId);
                dto.setSeatNumber(seat.getSeatNumber());
                dto.setIsAvailable(seat.isAvailable());
                dto.setSeatType(seat.getSeatType());
                dto.setCreatedAt(seat.getCreatedAt());
                return dto;
            })
            .collect(Collectors.toList());
        logger.info("Retrieved {} seats for bus ID: {}", seatDTOs.size(), busId);
        return seatDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> findCustomerBookings(Long customerId) {
        logger.debug("Fetching bookings for customer ID: {}", customerId);
        customerRepository.findById(customerId)
            .orElseThrow(() -> {
                logger.error("Customer not found with ID: {}", customerId);
                return new CustomerNotFoundException("Customer not found with ID: " + customerId);
            });
        List<BookingDTO> bookings = bookingService.findByUserId(customerId.intValue());
        logger.info("Retrieved {} bookings for customer ID: {}", bookings.size(), customerId);
        return bookings;
    }

    @Override
    @Transactional
    public BookingDTO bookTicket(Long customerId, Integer busId, String seatNumber) {
        logger.debug("Booking ticket for customer ID: {} on bus ID: {} for seat: {}", customerId, busId, seatNumber);
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> {
                logger.error("Customer not found with ID: {}", customerId);
                return new CustomerNotFoundException("Customer not found with ID: " + customerId);
            });

        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });

        List<Schedule> schedules = scheduleRepository.findByBusBusId(busId);
        Schedule schedule = schedules.stream()
            .filter(s -> s.getDepartureTime().isAfter(LocalDateTime.now()))
            .findFirst()
            .orElseThrow(() -> {
                logger.error("No valid schedule found for bus ID: {}", busId);
                return new ResourceNotFoundException("Schedule", "Bus ID", busId);
            });

        Seat seat = seatRepository.findByBusBusIdAndSeatNumber(busId, seatNumber)
            .orElseThrow(() -> {
                logger.error("Seat {} not found for bus ID: {}", seatNumber, busId);
                return new ResourceNotFoundException("Seat", "Number", seatNumber);
            });

        if (!seat.isAvailable()) {
            logger.error("Seat {} is not available for bus ID: {}", seatNumber, busId);
            throw new IllegalStateException("Seat " + seatNumber + " is not available");
        }

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBusId(busId);
        bookingDTO.setUserId(customerId.intValue());
        bookingDTO.setSeatId(seat.getSeatId());
        bookingDTO.setScheduleId(schedule.getScheduleId());
        bookingDTO.setBookingDate(LocalDateTime.now());
        bookingDTO.setStatus("CONFIRMED");
        bookingDTO.setFare(new BigDecimal("500.00")); // Default fare
        BookingDTO savedBooking = bookingService.save(bookingDTO, customerId.intValue());
        logger.info("Booked ticket for customer ID: {} with booking ID: {}", customerId, savedBooking.getBookingId());
        return savedBooking;
    }

    private CustomerDTO mapToCustomerDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setMobile(customer.getMobile());
        return dto;
    }
}