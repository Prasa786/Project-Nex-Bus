# Bus Ticket Booking System - Backend Service

![Java](https://img.shields.io/badge/Java-17-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-6DB33F?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Authentication-000000?logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?logo=apachemaven)

## Table of Contents
- [System Overview](#system-overview)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation Guide](#installation-guide)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)
- [Support](#support)

## System Overview

The Bus Ticket Booking System is a robust backend service built with Spring Boot that provides:

- Multi-role access control (Admin, Bus Operator, Customer)
- Comprehensive bus and route management
- Real-time seat availability tracking
- Secure payment processing
- Automated email notifications
- RESTful API for frontend integration

## Architecture


graph TD
    A[Client] --> B[Spring Boot Application]
    B --> C[Spring Security]
    B --> D[Spring Data JPA]
    B --> E[Spring Mail]
    D --> F[MySQL Database]
    C --> G[JWT Authentication]
    E --> H[SMTP Server]


Features
Core Functionality
User registration and authentication

JWT-based session management

Password encryption with BCrypt

Admin Module
Bus fleet management (CRUD operations)

Amenity configuration (WiFi, AC, etc.)

Route and schedule management

User administration

Operator Module
Real-time schedule updates

Seat availability management

Earnings reports

Customer Module
Bus search and filtering

Seat selection

Booking management

Payment processing

Ticket cancellation

##Technology Stack##
Component	Technology
Framework	Spring Boot 3.4.4
Language	Java 17
Build Tool	Maven
Database	MySQL 8.0
ORM	Spring Data JPA
Security	Spring Security + JWT
Email Service	Spring Mail
Logging	Log4j2
Testing	JUnit 5, Mockito
Documentation	Swagger UI
Installation Guide
Prerequisites
Java Development Kit 17

MySQL Server 8.0+

Maven 3.6+

Setup Instructions
Clone the repository:

bash
git clone https://github.com/<your-username>/BusTicketBooking.git
cd BusTicketBooking
Create and configure database:

sql
CREATE DATABASE Nexbus;
CREATE USER 'nexbus_user'@'localhost' IDENTIFIED BY 'securepassword';
GRANT ALL PRIVILEGES ON Nexbus.* TO 'nexbus_user'@'localhost';
FLUSH PRIVILEGES;
Configure application properties:

properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/Nexbus
spring.datasource.username=nexbus_user
spring.datasource.password=securepassword
spring.jpa.hibernate.ddl-auto=update
Build and run:

bash
mvn clean install
mvn spring-boot:run
Configuration
Essential Properties
properties
# Server
server.port=8090
server.servlet.context-path=/api

# JWT
jwt.secret=your-256-bit-secret
jwt.expiration=86400000 # 24 hours

# Email
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=no-reply@nexbus.com
spring.mail.password=email-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
Environment Variables
For production, configure these environment variables:

DB_URL

DB_USERNAME

DB_PASSWORD

JWT_SECRET

SMTP_CREDENTIALS

API Documentation
Access interactive API docs at: http://localhost:8090/swagger-ui.html

Sample Endpoints
Method	Endpoint	Description
POST	/api/auth/register	User registration
POST	/api/auth/login	User login
GET	/api/buses	List available buses
POST	/api/bookings	Create new booking
GET	/api/bookings/{id}	Get booking details
Database Schema


Key Entities:

users - User accounts and credentials

buses - Bus inventory and specifications

routes - Travel routes and stops

schedules - Departure/arrival timings

bookings - Reservation records

payments - Transaction history

Security
Authentication Flow
Client sends credentials to /api/auth/login

Server validates and returns JWT

Client includes JWT in Authorization header

Server verifies JWT for each request

Security Measures
Password hashing with BCrypt

JWT signature verification

Role-based endpoint authorization

CSRF protection

CORS configuration

Input validation

Testing
Test Coverage
Unit tests: 80% coverage

Integration tests: 65% coverage

Security tests: OWASP ZAP scans

Running Tests
bash
mvn test
Deployment
Production Recommendations
Docker containerization

Kubernetes orchestration

AWS RDS for MySQL

CI/CD pipeline with GitHub Actions

Docker Setup
dockerfile
FROM openjdk:17-jdk-slim
COPY target/busticketbooking-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
Contributing
Fork the repository

Create your feature branch (git checkout -b feature/your-feature)

Commit your changes (git commit -m 'Add some feature')

Push to the branch (git push origin feature/your-feature)

Open a Pull Request

Coding Standards
Follow Google Java Style Guide

Include Javadoc for all public methods

Write unit tests for new features

Keep commits atomic

License
MIT License

Copyright (c) 2025 NexBus


Support
For technical support, please contact:

Prasanna R
Email: prasannarps786@gmail.com
GitHub: @Prasa786

Please include the following in support requests:

Detailed description of the issue

Steps to reproduce

Screenshots/logs (if applicable)

Environment details
