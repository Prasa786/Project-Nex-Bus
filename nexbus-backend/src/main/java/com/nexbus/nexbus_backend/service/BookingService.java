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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    // Static refund policy thresholds
    private static final long FULL_REFUND_HOURS_SINCE_BOOKING = 24;
    private static final long FULL_REFUND_HOURS_BEFORE_DEPARTURE = 24;
    private static final long PARTIAL_REFUND_HOURS_BEFORE_DEPARTURE = 12;
    private static final double PARTIAL_REFUND_PERCENTAGE = 0.5;

    private final BookingRepository bookingRepository;
    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final SeatService seatService;
    private final ScheduleRepository scheduleRepository;
    private final EmailService emailService;

    public BookingService(BookingRepository bookingRepository, BusRepository busRepository,
                          SeatRepository seatRepository, UserRepository userRepository,
                          SeatService seatService, ScheduleRepository scheduleRepository,
                          EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.seatService = seatService;
        this.scheduleRepository = scheduleRepository;
        this.emailService = emailService;
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

        emailService.sendBookingConfirmationEmail(bookingDTO);

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

        emailService.sendBookingConfirmationEmail(bookingDTO);

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

        BookingDTO bookingDTO = mapToDTO(booking);
        emailService.sendCancellationEmail(bookingDTO);

        logger.info("Cancelled booking with ID: {}", id);
        return bookingDTO;
    }

    @Transactional
    public BigDecimal processRefund(Integer id, Integer userId) {
        logger.debug("Processing refund for booking ID: {} by user ID: {}", id, userId);
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

        BigDecimal refundAmount = calculateRefundAmount(booking);
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("No refund applicable for booking ID: {}", id);
        } else {
            logger.info("Refund amount calculated for booking ID: {} is {}", id, refundAmount);
        }

        booking.setStatus("REFUNDED");
        Seat seat = booking.getSeat();
        seat.setAvailable(true);
        seat.setBooking(null);
        seatRepository.save(seat);

        booking = bookingRepository.save(booking);

        BookingDTO bookingDTO = mapToDTO(booking);
        bookingDTO.setFare(refundAmount); // Update DTO with actual refunded amount
        emailService.sendRefundEmail(bookingDTO);

        logger.info("Refunded booking with ID: {} with amount: {}", id, refundAmount);
        return refundAmount;
    }

    @Transactional(readOnly = true)
    public BigDecimal estimateRefundAmount(Integer id, Integer userId) {
        logger.debug("Estimating refund for booking ID: {} by user ID: {}", id, userId);
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Booking not found with ID: {}", id);
                return new ResourceNotFoundException("Booking", "ID", id);
            });

        if (!isBookingOwner(id, userId) && !isBookingOperator(id, userId)) {
            logger.error("User ID: {} is not authorized to view refund estimate for booking ID: {}", userId, id);
            throw new SecurityException("User is not authorized to view refund estimate");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "REFUNDED".equals(booking.getStatus())) {
            logger.warn("Booking ID: {} is already cancelled or refunded, no refund estimate available", id);
            return BigDecimal.ZERO;
        }

        BigDecimal estimatedRefund = calculateRefundAmount(booking);
        logger.info("Estimated refund for booking ID: {} is {}", id, estimatedRefund);
        return estimatedRefund;
    }

    @Transactional(readOnly = true)
    public String getRefundPolicy() {
        return String.format("""
            Refund Policy:
            - 100%% refund if cancelled within %d hours of booking and at least %d hours before departure.
            - %.0f%% refund if cancelled after %d hours of booking but at least %d hours before departure.
            - No refund if cancelled less than %d hours before departure.
            """,
            FULL_REFUND_HOURS_SINCE_BOOKING,
            FULL_REFUND_HOURS_BEFORE_DEPARTURE,
            PARTIAL_REFUND_PERCENTAGE * 100,
            FULL_REFUND_HOURS_SINCE_BOOKING,
            PARTIAL_REFUND_HOURS_BEFORE_DEPARTURE,
            PARTIAL_REFUND_HOURS_BEFORE_DEPARTURE);
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
        if (booking == null) {
            logger.error("Attempted to map null Booking to DTO");
            throw new IllegalArgumentException("Booking cannot be null");
        }

        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());

        if (booking.getBus() != null) {
            dto.setBusId(booking.getBus().getBusId());
        } else {
            logger.warn("Booking ID {} has null Bus reference", booking.getBookingId());
            throw new IllegalStateException("Booking has no associated Bus");
        }

        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getUserId());
        } else {
            logger.warn("Booking ID {} has null User reference", booking.getBookingId());
            throw new IllegalStateException("Booking has no associated User");
        }

        if (booking.getSeat() != null) {
            dto.setSeatId(booking.getSeat().getSeatId());
        } else {
            logger.warn("Booking ID {} has null Seat reference", booking.getBookingId());
            throw new IllegalStateException("Booking has no associated Seat");
        }

        dto.setScheduleId(booking.getSchedule() != null ? booking.getSchedule().getScheduleId() : null);
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        dto.setFare(booking.getFare());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        return dto;
    }

    private BigDecimal calculateRefundAmount(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingTime = booking.getCreatedAt();
        LocalDateTime departureTime = booking.getSchedule().getDepartureTime();

        long hoursSinceBooking = ChronoUnit.HOURS.between(bookingTime, now);
        long hoursUntilDeparture = ChronoUnit.HOURS.between(now, departureTime);

        logger.debug("Booking ID: {}, Hours since booking: {}, Hours until departure: {}",
            booking.getBookingId(), hoursSinceBooking, hoursUntilDeparture);

        BigDecimal fare = booking.getFare();

        if (hoursSinceBooking <= FULL_REFUND_HOURS_SINCE_BOOKING && hoursUntilDeparture >= FULL_REFUND_HOURS_BEFORE_DEPARTURE) {
            logger.debug("Booking ID: {} qualifies for full refund", booking.getBookingId());
            return fare;
        } else if (hoursUntilDeparture >= PARTIAL_REFUND_HOURS_BEFORE_DEPARTURE) {
            logger.debug("Booking ID: {} qualifies for partial refund ({}%)", booking.getBookingId(), PARTIAL_REFUND_PERCENTAGE * 100);
            return fare.multiply(BigDecimal.valueOf(PARTIAL_REFUND_PERCENTAGE));
        } else {
            logger.debug("Booking ID: {} does not qualify for refund (less than {} hours to departure)", 
                booking.getBookingId(), PARTIAL_REFUND_HOURS_BEFORE_DEPARTURE);
            return BigDecimal.ZERO;
        }
    }
}