//package com.nexbus.nexbus_backend.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class RequestLoggingFilter extends OncePerRequestFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        logger.info("Incoming request: method={}, uri={}, headers={}", 
//            request.getMethod(), request.getRequestURI(), request.getHeader("Authorization"));
//        filterChain.doFilter(request, response);
//    }
//}