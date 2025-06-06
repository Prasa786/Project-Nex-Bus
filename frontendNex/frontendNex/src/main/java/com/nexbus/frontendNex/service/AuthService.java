package com.nexbus.frontendNex.service;

import com.nexbus.frontendNex.dto.LoginRequest;
import com.nexbus.frontendNex.dto.LoginResponse;
import com.nexbus.frontendNex.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BACKEND_URL = "http://localhost:9090/auth/api";

    public LoginResponse login(LoginRequest loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<LoginResponse> response = restTemplate.exchange(
            BACKEND_URL + "/login",
            HttpMethod.POST,
            requestEntity,
            LoginResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new ApiException("Login failed: " + response.getStatusCode());
        }
    }
}