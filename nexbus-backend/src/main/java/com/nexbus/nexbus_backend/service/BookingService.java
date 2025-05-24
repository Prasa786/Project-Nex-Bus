<<<<<<< HEAD
package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.BookingDTO;
import com.nexbus.nexbus_backend.dto.SeatDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Booking;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Schedule;
import com.nexbus.nexbus_backend.model.Seat;
import com.nexbus.nexbus_backend.model.User;
import com.nexbus.nexbus_backend.repository.BookingRepository;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.ScheduleRepository;
import com.nexbus.nexbus_backend.repository.SeatRepository;
import com.nexbus.nexbus_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final SeatService seatService;
    private final ScheduleRepository scheduleRepository;

    public BookingService(BookingRepository bookingRepository, BusRepository busRepository,
                          SeatRepository seatRepository, UserRepository userRepository,
                          SeatService seatService, ScheduleRepository scheduleRepository) {
        this.bookingRepository = bookingRepository;
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.seatService = seatService;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> findAll() {
        logger.debug("Fetching all bookings");
        List<Booking> bookings = bookingRepository.findAll();
        logger.info("Retrieved {} bookings", bookings.size());
        return bookings.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingDTO findById(Integer id) {
        logger.debug("Fetching booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });
        logger.info("Found booking with ID: {}", id);
        return mapToDTO(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> findByBusId(Integer busId) {
        logger.debug("Fetching bookings for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<Booking> bookings = bookingRepository.findByBus(bus);
        logger.info("Retrieved {} bookings for bus ID: {}", bookings.size(), busId);
        return bookings.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> findByUserId(Integer userId) {
        logger.debug("Fetching bookings for user ID: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new ResourceNotFoundException("User", "ID", userId);
            });
        List<Booking> bookings = bookingRepository.findByUser(user);
        logger.info("Retrieved {} bookings for user ID: {}", bookings.size(), userId);
        return bookings.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO save(BookingDTO bookingDTO, Integer userId) {
        logger.debug("Saving new booking for bus ID: {}", bookingDTO.getBusId());
        if (!bookingDTO.getUserId().equals(userId)) {
            logger.error("User ID {} does not match booking user ID {}", userId, bookingDTO.getUserId());
            throw new SecurityException("Unauthorized user ID for booking");
        }

        if (bookingDTO.getBookingDate().isBefore(LocalDateTime.now())) {
            logger.error("Booking date {} is in the past", bookingDTO.getBookingDate());
            throw new IllegalArgumentException("Booking date cannot be in the past");
        }

        Bus bus = busRepository.findById(bookingDTO.getBusId())
            .orElseThrow(() -> new ResourceNotFoundException("Bus", "ID", bookingDTO.getBusId()));
        User user = userRepository.findById(bookingDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "ID", bookingDTO.getUserId()));
        Seat seat = seatRepository.findById(bookingDTO.getSeatId())
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "ID", bookingDTO.getSeatId()));
        Schedule schedule = scheduleRepository.findById(bookingDTO.getScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("Schedule", "ID", bookingDTO.getScheduleId()));

        if (!seat.getBus().getBusId().equals(bus.getBusId())) {
            logger.error("Seat ID: {} does not belong to bus ID: {}", bookingDTO.getSeatId(), bus.getBusId());
            throw new IllegalStateException("Seat does not belong to the specified bus");
        }

        if (!schedule.getBus().getBusId().equals(bus.getBusId())) {
            logger.error("Schedule ID: {} does not belong to bus ID: {}", bookingDTO.getScheduleId(), bus.getBusId());
            throw new IllegalStateException("Schedule does not belong to the specified bus");
        }

        List<SeatDTO> availableSeats = seatService.findAvailableSeatsByBusId(bus.getBusId());
        if (!availableSeats.stream().anyMatch(s -> s.getSeatId().equals(seat.getSeatId()))) {
            logger.error("Seat ID: {} is not available for bus ID: {}", seat.getSeatId(), bus.getBusId());
            throw new IllegalStateException("Seat is not available for booking");
        }

        seat.setAvailable(false);
        seat.setBooking(null);
        seatRepository.save(seat);

        Booking booking = new Booking();
        booking.setBus(bus);
        booking.setUser(user);
        booking.setSeat(seat);
        booking.setSchedule(schedule);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStatus("CONFIRMED");
        booking.setFare(bookingDTO.getFare());
        booking = bookingRepository.save(booking);

        seat.setBooking(booking);
        seatRepository.save(seat);

        logger.info("Saved booking with ID: {}", booking.getBookingId());
        return mapToDTO(booking);
    }

    @Transactional
    public BookingDTO update(Integer id, BookingDTO bookingDTO) {
        logger.debug("Updating booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "ID", id));

        if (bookingDTO.getBookingDate().isBefore(LocalDateTime.now())) {
            logger.error("Updated booking date {} is in the past", bookingDTO.getBookingDate());
            throw new IllegalArgumentException("Booking date cannot be in the past");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "REFUNDED".equals(booking.getStatus())) {
            logger.error("Booking ID: {} is already cancelled or refunded", id);
            throw new IllegalStateException("Cannot update a booking that is cancelled or refunded");
        }

        Bus bus = busRepository.findById(bookingDTO.getBusId())
            .orElseThrow(() -> new ResourceNotFoundException("Bus", "ID", bookingDTO.getBusId()));
        User user = userRepository.findById(bookingDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "ID", bookingDTO.getUserId()));
        Seat newSeat = seatRepository.findById(bookingDTO.getSeatId())
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "ID", bookingDTO.getSeatId()));
        Schedule schedule = scheduleRepository.findById(bookingDTO.getScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("Schedule", "ID", bookingDTO.getScheduleId()));

        if (!newSeat.getBus().getBusId().equals(bus.getBusId())) {
            logger.error("Seat ID: {} does not belong to bus ID: {}", bookingDTO.getSeatId(), bus.getBusId());
            throw new IllegalStateException("Seat does not belong to the specified bus");
        }

        if (!schedule.getBus().getBusId().equals(bus.getBusId())) {
            logger.error("Schedule ID: {} does not belong to bus ID: {}", bookingDTO.getScheduleId(), bus.getBusId());
            throw new IllegalStateException("Schedule does not belong to the specified bus");
        }

        if (!booking.getSeat().getSeatId().equals(newSeat.getSeatId())) {
            List<SeatDTO> availableSeats = seatService.findAvailableSeatsByBusId(bus.getBusId());
            if (!availableSeats.stream().anyMatch(s -> s.getSeatId().equals(newSeat.getSeatId()))) {
                logger.error("New seat ID: {} is not available for bus ID: {}", newSeat.getSeatId(), bus.getBusId());
                throw new IllegalStateException("New seat is not available for booking");
            }

            Seat oldSeat = booking.getSeat();
            oldSeat.setAvailable(true);
            oldSeat.setBooking(null);
            seatRepository.save(oldSeat);

            newSeat.setAvailable(false);
            newSeat.setBooking(booking);
            seatRepository.save(newSeat);

            booking.setSeat(newSeat);
        }

        booking.setBus(bus);
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStatus("CONFIRMED");
        booking.setFare(bookingDTO.getFare());
        booking = bookingRepository.save(booking);

        logger.info("Updated booking with ID: {}", id);
        return mapToDTO(booking);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });

        if ("CANCELLED".equals(booking.getStatus()) || "REFUNDED".equals(booking.getStatus())) {
            logger.error("Booking ID: {} is already cancelled or refunded", id);
            throw new IllegalStateException("Booking is already cancelled or refunded");
        }

        Seat seat = booking.getSeat();
        seat.setAvailable(true);
        seat.setBooking(null);
        seatRepository.save(seat);

        bookingRepository.delete(booking);
        logger.info("Deleted booking with ID: {}", id);
    }

    @Transactional
    public BookingDTO cancelBooking(Integer id, Integer userId) {
        logger.debug("Cancelling booking with ID: {} by user ID: {}", id, userId);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });

        if (!isBookingOwner(id, userId) && !isBookingOperator(id, userId)) {
            logger.error("User ID: {} is not authorized to cancel booking ID: {}", userId, id);
            throw new SecurityException("User is not authorized to cancel this booking");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "REFUNDED".equals(booking.getStatus())) {
            logger.error("Booking ID: {} is already cancelled or refunded", id);
            throw new IllegalStateException("Booking is already cancelled or refunded");
        }

        booking.setStatus("CANCELLED");
        Seat seat = booking.getSeat();
        seat.setAvailable(true);
        seat.setBooking(null);
        seatRepository.save(seat);

        booking = bookingRepository.save(booking);
        logger.info("Cancelled booking with ID: {}", id);
        return mapToDTO(booking);
    }

    @Transactional
    public BigDecimal processRefund(Integer id, Integer userId) {
        logger.debug("Refunding booking with ID: {} by user ID: {}", id, userId);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });

        if (!isBookingOwner(id, userId) && !isBookingOperator(id, userId)) {
            logger.error("User ID: {} is not authorized to refund booking ID: {}", userId, id);
            throw new SecurityException("User is not authorized to refund this booking");
        }

        if (!"CANCELLED".equals(booking.getStatus())) {
            logger.error("Booking ID: {} is not cancelled, cannot refund", id);
            throw new IllegalStateException("Booking must be cancelled before refunding");
        }

        if ("REFUNDED".equals(booking.getStatus())) {
            logger.error("Booking ID: {} is already refunded", id);
            throw new IllegalStateException("Booking is already refunded");
        }

        // Check refund window (24 hours from booking date)
        LocalDateTime now = LocalDateTime.now();
        if (booking.getBookingDate().plusHours(24).isBefore(now)) {
            logger.warn("Booking ID: {} is past refund window", id);
            throw new IllegalStateException("Refund window has expired");
        }

        booking.setStatus("REFUNDED");
        Seat seat = booking.getSeat();
        seat.setAvailable(true);
        seat.setBooking(null);
        seatRepository.save(seat);

        booking = bookingRepository.save(booking);
        logger.info("Refunded booking with ID: {} with amount: {}", id, booking.getFare());
        return booking.getFare();
    }

    @Transactional(readOnly = true)
    public boolean isBookingOwner(Integer bookingId, Integer userId) {
        logger.debug("Checking if user ID: {} owns booking ID: {}", userId, bookingId);
        return bookingRepository.findById(bookingId)
            .map(booking -> {
                boolean isOwner = booking.getUser().getUserId().equals(userId);
                logger.debug("User ID: {} is {} the owner of booking ID: {}", userId, isOwner ? "" : "not", bookingId);
                return isOwner;
            })
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", bookingId);
                return new ResourceNotFoundException("Booking", "ID", bookingId);
            });
    }

    @Transactional(readOnly = true)
    public boolean isBookingOperator(Integer bookingId, Integer userId) {
        logger.debug("Checking if user ID: {} is operator for booking ID: {}", userId, bookingId);
        return bookingRepository.findById(bookingId)
            .map(booking -> busRepository.findById(booking.getBus().getBusId())
                .map(bus -> bus.getOperator().getUser().getUserId().equals(userId))
                .orElse(false))
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", bookingId);
                return new ResourceNotFoundException("Booking", "ID", bookingId);
            });
    }

    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setBusId(booking.getBus().getBusId());
        dto.setUserId(booking.getUser().getUserId());
        dto.setSeatId(booking.getSeat().getSeatId());
        dto.setScheduleId(booking.getSchedule() != null ? booking.getSchedule().getScheduleId() : null);
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        dto.setFare(booking.getFare());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }
=======
package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.BookingDTO;
import com.nexbus.nexbus_backend.dto.SeatDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Booking;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Schedule;
import com.nexbus.nexbus_backend.model.Seat;
import com.nexbus.nexbus_backend.model.User;
import com.nexbus.nexbus_backend.repository.BookingRepository;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.ScheduleRepository;
import com.nexbus.nexbus_backend.repository.SeatRepository;
import com.nexbus.nexbus_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final SeatService seatService;
    private final ScheduleRepository scheduleRepository;

    public BookingService(BookingRepository bookingRepository, BusRepository busRepository,
                          SeatRepository seatRepository, UserRepository userRepository,
                          SeatService seatService, ScheduleRepository scheduleRepository) {
        this.bookingRepository = bookingRepository;
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.seatService = seatService;
        this.scheduleRepository = scheduleRepository;
    }
    public List<BookingDTO> findAll() {
        logger.debug("Fetching all bookings");
        List<BookingDTO> bookings = bookingRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} bookings", bookings.size());
        return bookings;
    }

    public BookingDTO findById(Integer id) {
        logger.debug("Fetching booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });
        logger.info("Found booking with ID: {}", id);
        return mapToDTO(booking);
    }

    public List<BookingDTO> findByBusId(Integer busId) {
        logger.debug("Fetching bookings for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<BookingDTO> bookings = bookingRepository.findByBus(bus).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} bookings for bus ID: {}", bookings.size(), busId);
        return bookings;
    }

    public List<BookingDTO> findByUserId(Integer userId) {
        logger.debug("Fetching bookings for user ID: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new ResourceNotFoundException("User", "ID", userId);
            });
        List<BookingDTO> bookings = bookingRepository.findByUser(user).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} bookings for user ID: {}", bookings.size(), userId);
        return bookings;
    }

    @Transactional
    public BookingDTO save(BookingDTO bookingDTO) {
        logger.debug("Saving new booking for bus ID: {}", bookingDTO.getBusId());

        if (bookingDTO.getBookingDate().isBefore(LocalDateTime.now())) {
            logger.error("Booking date {} is in the past", bookingDTO.getBookingDate());
            throw new IllegalArgumentException("Booking date cannot be in the past");
        }

        Bus bus = busRepository.findById(bookingDTO.getBusId())
            .orElseThrow(() -> new ResourceNotFoundException("Bus", "ID", bookingDTO.getBusId()));
        User user = userRepository.findById(bookingDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "ID", bookingDTO.getUserId()));
        Seat seat = seatRepository.findById(bookingDTO.getSeatId())
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "ID", bookingDTO.getSeatId()));
        Schedule schedule = null;
        if (bookingDTO.getScheduleId() != null) {
            schedule = scheduleRepository.findById(bookingDTO.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "ID", bookingDTO.getScheduleId()));
        }

        if (!seat.getBus().getBusId().equals(bus.getBusId())) {
            logger.error("Seat with ID: {} does not belong to bus ID: {}", bookingDTO.getSeatId(), bus.getBusId());
            throw new IllegalStateException("Seat does not belong to the specified bus");
        }

        if (schedule != null && !schedule.getBus().getBusId().equals(bus.getBusId())) {
            logger.error("Schedule with ID: {} does not belong to bus ID: {}", bookingDTO.getScheduleId(), bus.getBusId());
            throw new IllegalStateException("Schedule does not belong to the specified bus");
        }

        List<SeatDTO> availableSeats = seatService.findAvailableSeatsByBusId(bus.getBusId());
        if (!availableSeats.stream().anyMatch(s -> s.getSeatId().equals(seat.getSeatId()))) {
            logger.error("Seat with ID: {} is not available for bus ID: {}", seat.getSeatId(), bus.getBusId());
            throw new IllegalStateException("Seat is not available for booking");
        }

        seat.setAvailable(false);
        seat.setBooking(null);
        seatRepository.save(seat);

        Booking booking = new Booking();
        booking.setBus(bus);
        booking.setUser(user);
        booking.setSeat(seat);
        booking.setSchedule(schedule);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStatus("CONFIRMED");
        booking = bookingRepository.save(booking);

        seat.setBooking(booking);
        seatRepository.save(seat);

        logger.info("Saved booking with ID: {}", booking.getBookingId());
        return mapToDTO(booking);
    }

    @Transactional
    public BookingDTO update(Integer id, BookingDTO bookingDTO) {
        logger.debug("Updating booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "ID", id));

        if (bookingDTO.getBookingDate().isBefore(LocalDateTime.now())) {
            logger.error("Updated booking date {} is in the past", bookingDTO.getBookingDate());
            throw new IllegalArgumentException("Booking date cannot be in the past");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "REFUNDED".equals(booking.getStatus())) {
            logger.error("Booking with ID: {} is already cancelled or refunded", id);
            throw new IllegalStateException("Cannot update a booking that is cancelled or refunded");
        }

        Bus bus = busRepository.findById(bookingDTO.getBusId())
            .orElseThrow(() -> new ResourceNotFoundException("Bus", "ID", bookingDTO.getBusId()));
        User user = userRepository.findById(bookingDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "ID", bookingDTO.getUserId()));
        Seat newSeat = seatRepository.findById(bookingDTO.getSeatId())
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "ID", bookingDTO.getSeatId()));
        Schedule schedule = null;
        if (bookingDTO.getScheduleId() != null) {
            schedule = scheduleRepository.findById(bookingDTO.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "ID", bookingDTO.getScheduleId()));
        }

        if (!newSeat.getBus().getBusId().equals(bus.getBusId())) {
            logger.error("Seat with ID: {} does not belong to bus ID: {}", bookingDTO.getSeatId(), bus.getBusId());
            throw new IllegalStateException("Seat does not belong to the specified bus");
        }

        if (schedule != null && !schedule.getBus().getBusId().equals(bus.getBusId())) {
            logger.error("Schedule with ID: {} does not belong to bus ID: {}", bookingDTO.getScheduleId(), bus.getBusId());
            throw new IllegalStateException("Schedule does not belong to the specified bus");
        }

        if (!booking.getSeat().getSeatId().equals(newSeat.getSeatId())) {
            List<SeatDTO> availableSeats = seatService.findAvailableSeatsByBusId(bus.getBusId());
            if (!availableSeats.stream().anyMatch(s -> s.getSeatId().equals(newSeat.getSeatId()))) {
                logger.error("New seat with ID: {} is not available for bus ID: {}", newSeat.getSeatId(), bus.getBusId());
                throw new IllegalStateException("New seat is not available for booking");
            }

            Seat oldSeat = booking.getSeat();
            oldSeat.setAvailable(true);
            oldSeat.setBooking(null);
            seatRepository.save(oldSeat);

            newSeat.setAvailable(false);
            newSeat.setBooking(booking);
            seatRepository.save(newSeat);

            booking.setSeat(newSeat);
        }

        booking.setBus(bus);
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStatus("CONFIRMED");
        booking = bookingRepository.save(booking);

        logger.info("Updated booking with ID: {}", id);
        return mapToDTO(booking);
    }
    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });

        if ("CANCELLED".equals(booking.getStatus()) || "REFUNDED".equals(booking.getStatus())) {
            logger.error("Booking with ID: {} is already cancelled or refunded", id);
            throw new IllegalStateException("Booking is already cancelled or refunded");
        }

        // Mark the seat as available and clear the booking association
        Seat seat = booking.getSeat();
        seat.setAvailable(true);
        seat.setBooking(null);
        seatRepository.save(seat);

        bookingRepository.delete(booking);
        logger.info("Deleted booking with ID: {}", id);
    }

    @Transactional
    public BookingDTO cancelBooking(Integer id, Integer operatorId) {
        logger.debug("Cancelling booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });

        if (!booking.getBus().getOperator().getOperatorId().equals(operatorId)) {
            logger.error("Operator ID: {} does not own the bus for booking ID: {}", operatorId, id);
            throw new IllegalStateException("Operator does not own this bus");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "REFUNDED".equals(booking.getStatus())) {
            logger.error("Booking with ID: {} is already cancelled or refunded", id);
            throw new IllegalStateException("Booking is already cancelled or refunded");
        }

        booking.setStatus("CANCELLED");
        Seat seat = booking.getSeat();
        seat.setAvailable(true);
        seat.setBooking(null);
        seatRepository.save(seat);

        booking = bookingRepository.save(booking);
        logger.info("Cancelled booking with ID: {}", id);
        return mapToDTO(booking);
    }

    @Transactional
    public BigDecimal processRefund(Integer id, Integer operatorId) {
        logger.debug("Refunding booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });

        if (!booking.getBus().getOperator().getOperatorId().equals(operatorId)) {
            logger.error("Operator ID: {} does not own the bus for booking ID: {}", operatorId, id);
            throw new IllegalStateException("Operator does not own this bus");
        }

        if (!"CANCELLED".equals(booking.getStatus())) {
            logger.error("Booking with ID: {} is not cancelled, cannot refund", id);
            throw new IllegalStateException("Booking must be cancelled before refunding");
        }

        if ("REFUNDED".equals(booking.getStatus())) {
            logger.error("Booking with ID: {} is already refunded", id);
            throw new IllegalStateException("Booking is already refunded");
        }

        booking.setStatus("REFUNDED");
        Seat seat = booking.getSeat();
        seat.setAvailable(true);
        seat.setBooking(null);
        seatRepository.save(seat);

        booking = bookingRepository.save(booking);
        logger.info("Refunded booking with ID: {}", id);

        // Placeholder for refund amount calculation (e.g., based on booking cost, cancellation policy, etc.)
        BigDecimal refundAmount = new BigDecimal("50.00"); // Example refund amount
        return refundAmount;
    }

    public boolean isBookingOwner(Integer bookingId, Integer userId) {
        logger.debug("Checking if user ID: {} owns booking ID: {}", userId, bookingId);
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", bookingId);
                return new ResourceNotFoundException("Booking", "ID", bookingId);
            });
        boolean isOwner = booking.getUser().getUserId().equals(userId);
        logger.info("User ID: {} owns booking ID: {} - {}", userId, bookingId, isOwner);
        return isOwner;
    }

    public boolean isBookingOperator(Integer bookingId, Integer operatorId) {
        logger.debug("Checking if operator ID: {} operates booking ID: {}", operatorId, bookingId);
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", bookingId);
                return new ResourceNotFoundException("Booking", "ID", bookingId);
            });
        boolean isOperator = booking.getBus().getOperator().getOperatorId().equals(operatorId);
        logger.info("Operator ID: {} operates booking ID: {} - {}", operatorId, bookingId, isOperator);
        return isOperator;
    }

    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setBusId(booking.getBus().getBusId());
        dto.setUserId(booking.getUser().getUserId());
        dto.setSeatId(booking.getSeat().getSeatId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}