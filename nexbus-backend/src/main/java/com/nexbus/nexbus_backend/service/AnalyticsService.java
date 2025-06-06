package com.nexbus.nexbus_backend.service;

   import com.nexbus.nexbus_backend.dto.AnalyticsDTO;
   import com.nexbus.nexbus_backend.model.Role.RoleName;
   import com.nexbus.nexbus_backend.repository.BookingRepository;
   import com.nexbus.nexbus_backend.repository.UserRepository;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.stereotype.Service;

   @Service
   public class AnalyticsService {

       private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

       private final BookingRepository bookingRepository;
       private final UserRepository userRepository;

       public AnalyticsService(BookingRepository bookingRepository, UserRepository userRepository) {
           this.bookingRepository = bookingRepository;
           this.userRepository = userRepository;
       }

       public AnalyticsDTO getBookingAnalytics() {
           logger.debug("Fetching booking analytics");
           AnalyticsDTO analytics = new AnalyticsDTO();
           analytics.setTotalConfirmedBookings(bookingRepository.countConfirmedBookings());
           analytics.setTotalRevenue(bookingRepository.sumConfirmedFares() != null ? bookingRepository.sumConfirmedFares() : 0.0);
           analytics.setTotalPassengers(userRepository.countByRole_RoleName(RoleName.CUSTOMER));
           logger.info("Retrieved booking analytics");
           return analytics;
       }
   }