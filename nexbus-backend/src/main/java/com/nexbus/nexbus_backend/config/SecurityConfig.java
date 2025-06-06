package com.nexbus.nexbus_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import com.nexbus.nexbus_backend.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/login", "/auth/register", "/error").permitAll()
                // Admin-only endpoints
                .requestMatchers("/api/users/**", "/api/roles/**").hasAuthority("ADMIN")
                // Operator endpoints
                .requestMatchers(HttpMethod.GET, "/api/operators/**").hasAnyAuthority("ADMIN", "BUSOPERATOR")
                .requestMatchers(HttpMethod.POST, "/api/operators/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/operators/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/operators/**").hasAuthority("ADMIN")
                // Bus, seat, route endpoints
                .requestMatchers(HttpMethod.GET, "/api/buses/**", "/api/seats/**", "/api/routes/**").hasAnyAuthority("ADMIN", "BUSOPERATOR", "CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/buses/**", "/api/seats/**", "/api/routes/**").hasAnyAuthority("ADMIN", "BUSOPERATOR")
                .requestMatchers(HttpMethod.PUT, "/api/buses/**", "/api/seats/**", "/api/routes/**").hasAnyAuthority("ADMIN", "BUSOPERATOR")
                .requestMatchers("/api/routes/**").hasAnyRole("ADMIN", "BUSOPERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/buses/**", "/api/seats/**", "/api/routes/**").hasAnyAuthority("ADMIN", "BUSOPERATOR")
                // Booking endpoints
                .requestMatchers("/api/bookings/**").hasAnyAuthority("ADMIN", "BUSOPERATOR", "CUSTOMER")
                // Payment endpoints
                .requestMatchers(HttpMethod.GET, "/api/payments/**").hasAnyAuthority("ADMIN", "BUSOPERATOR")
                .requestMatchers(HttpMethod.POST, "/api/payments/**").hasAnyAuthority("ADMIN", "CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/api/payments/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/payments/**").hasAuthority("ADMIN")
                // Support endpoints
                .requestMatchers(HttpMethod.GET, "/api/support/**").hasAnyAuthority("ADMIN", "CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/support/**").hasAnyAuthority("ADMIN", "CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/api/support/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/support/**").hasAuthority("ADMIN")
                // Notification endpoints
                .requestMatchers(HttpMethod.GET, "/api/notifications/**").hasAnyAuthority("ADMIN", "CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasAnyAuthority("ADMIN", "CUSTOMER")
                // Report endpoints
                .requestMatchers("/api/reports/**").hasAuthority("ADMIN")
                // Catch-all
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}