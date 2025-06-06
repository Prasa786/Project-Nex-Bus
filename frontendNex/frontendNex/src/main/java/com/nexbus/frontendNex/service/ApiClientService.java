package com.nexbus.frontendNex.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ApiClientService {

    @Value("${backend.api.url:http://localhost:9090}")
    private String backendApiUrl;

    private final RestTemplate restTemplate;
    private final HttpSession httpSession;

    @Autowired
    public ApiClientService(RestTemplate restTemplate, HttpSession httpSession) {
        this.restTemplate = restTemplate;
        this.httpSession = httpSession;
    }

    public <T> T get(String endpoint, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders(null));
        ResponseEntity<T> response = restTemplate.exchange(
            backendApiUrl + endpoint,
            HttpMethod.GET,
            entity,
            responseType
        );
        return handleResponse(response);
    }

    public <T> T get(String endpoint, String authToken, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders(authToken));
        ResponseEntity<T> response = restTemplate.exchange(
            backendApiUrl + endpoint,
            HttpMethod.GET,
            entity,
            responseType
        );
        return handleResponse(response);
    }

    public <T> T post(String endpoint, Object body, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(body, createHeaders(null));
        ResponseEntity<T> response = restTemplate.exchange(
            backendApiUrl + endpoint,
            HttpMethod.POST,
            entity,
            responseType
        );
        return handleResponse(response);
    }

    public <T> T put(String endpoint, Object body, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(body, createHeaders(null));
        ResponseEntity<T> response = restTemplate.exchange(
            backendApiUrl + endpoint,
            HttpMethod.PUT,
            entity,
            responseType
        );
        return handleResponse(response);
    }

    public void delete(String endpoint) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders(null));
        ResponseEntity<Void> response = restTemplate.exchange(
            backendApiUrl + endpoint,
            HttpMethod.DELETE,
            entity,
            Void.class
        );
        handleResponse(response);
    }

    private HttpHeaders createHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = (authToken != null && !authToken.isBlank()) ? authToken : (String) httpSession.getAttribute("authToken");
        if (token != null && !token.isBlank()) {
            headers.set("Authorization", "Bearer " + token);
        }
        return headers;
    }

    private <T> T handleResponse(ResponseEntity<T> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Backend API error: " + response.getStatusCode());
        }
        return response.getBody();
    }
}