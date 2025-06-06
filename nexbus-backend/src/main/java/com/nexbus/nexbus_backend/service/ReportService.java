package com.nexbus.nexbus_backend.service;

   import com.nexbus.nexbus_backend.dto.ReportDTO;
   import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
   import com.nexbus.nexbus_backend.model.Booking;
   import com.nexbus.nexbus_backend.model.Support;
   import com.nexbus.nexbus_backend.repository.BookingRepository;
   import com.nexbus.nexbus_backend.repository.SupportRepository;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.stereotype.Service;

   import java.util.Arrays;
   import java.util.List;
   import java.util.stream.Collectors;

   @Service
   public class ReportService {

       private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

       private final BookingRepository bookingRepository;
       private final SupportRepository supportRepository;

       public ReportService(BookingRepository bookingRepository, SupportRepository supportRepository) {
           this.bookingRepository = bookingRepository;
           this.supportRepository = supportRepository;
       }

       public ReportDTO generateReport(String type) {
           logger.debug("Generating report for type: {}", type);
           ReportDTO report = new ReportDTO();
           report.setReportType(type);

           switch (type.toLowerCase()) {
               case "bookings":
                   report.setHeaders(Arrays.asList("Booking ID", "User ID", "Bus ID", "Seat ID", "Booking Date", "Fare", "Status"));
                   report.setRows(bookingRepository.findAll().stream()
                       .map(this::mapBookingToRow)
                       .collect(Collectors.toList()));
                   break;
               case "support":
                   report.setHeaders(Arrays.asList("Ticket ID", "User ID", "Subject", "Status", "Created At"));
                   report.setRows(supportRepository.findAll().stream()
                       .map(this::mapSupportToRow)
                       .collect(Collectors.toList()));
                   break;
               default:
                   logger.error("Invalid report type: {}", type);
                   throw new ResourceNotFoundException("Report", "type", type);
           }

           logger.info("Generated {} report with {} entries", type, report.getRows().size());
           return report;
       }

       private List<String> mapBookingToRow(Booking booking) {
           return Arrays.asList(
               booking.getBookingId().toString(),
               booking.getUser().getUserId().toString(),
               booking.getBus().getBusId().toString(),
               booking.getSeat().getSeatId().toString(),
               booking.getBookingDate().toString(),
               booking.getFare().toString(),
               booking.getStatus()
           );
       }

       private List<String> mapSupportToRow(Support support) {
           return Arrays.asList(
               support.getSupportId().toString(),
               support.getUser().getUserId().toString(),
               support.getSubject(),
               support.getStatus(),
               support.getCreatedAt().toString()
           );
       }
   }