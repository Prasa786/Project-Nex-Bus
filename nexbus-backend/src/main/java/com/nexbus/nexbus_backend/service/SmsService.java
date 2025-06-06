package com.nexbus.nexbus_backend.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public interface SmsService {
    void sendOtpSms(String phoneNumber, String userName);
    boolean validateOtp(String phoneNumber, String otp);
    String generateAndStoreOtp(String phoneNumber);
}

@Service
class SmsServiceImpl implements SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private final Map<String, OtpDetails> otpStorage = new HashMap<>();
    private static final int OTP_EXPIRY_MINUTES = 5;

    @PostConstruct
    public void initTwilio() {
        if (accountSid == null || authToken == null) {
            throw new IllegalStateException("Twilio credentials are not properly configured");
        }
        Twilio.init(accountSid, authToken);
    }

    @Override
    public void sendOtpSms(String phoneNumber, String userName) {
        String formattedPhone = formatPhoneNumber(phoneNumber);
        String otp = generateAndStoreOtp(formattedPhone);

        try {
            Message.creator(
                    new PhoneNumber(formattedPhone),
                    new PhoneNumber(twilioPhoneNumber),
                    "Dear " + userName + ", your OTP is: " + otp + ". It expires in 5 minutes."
            ).create();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP SMS: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateAndStoreOtp(String phoneNumber) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStorage.put(phoneNumber, new OtpDetails(otp, expiryTime));
        return otp;
    }

    @Override
    public boolean validateOtp(String phoneNumber, String userOtp) {
        String formattedPhone = formatPhoneNumber(phoneNumber);
        if (!otpStorage.containsKey(formattedPhone)) {
            return false; // No OTP exists
        }

        OtpDetails otpDetails = otpStorage.get(formattedPhone);
        if (otpDetails.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpStorage.remove(formattedPhone); // Remove expired OTP
            return false;
        }

        boolean isValid = otpDetails.getOtp().equals(userOtp);
        if (isValid) {
            otpStorage.remove(formattedPhone); // Clear OTP after successful validation
        }
        return isValid;
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        return "+91" + phoneNumber; // Default to +91 (India) if no country code
    }

    private static class OtpDetails {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OtpDetails(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}