BusTicketBooking Backend
Overview
The BusTicketBooking backend is a Spring Boot application designed to manage bus ticket reservations, including features for bus management, amenity assignments, user authentication, and email notifications. The application uses MySQL for data persistence, Spring Data JPA for database operations, Spring Security with JWT for authentication, and Spring Mail for email functionality.
This project is organized into four branches:

main: Contains the stable, production-ready code with core functionality.
admin: Includes admin-specific features and backend logic for administrative tasks (e.g., managing buses and amenities).
bus operator: Focuses on features for bus operators, such as schedule management.
user: Contains user-facing functionality, such as booking tickets and viewing available buses.

Technologies Used

Java: 17
Spring Boot: 3.4.4
Spring Data JPA: For database operations
Spring Security: For JWT-based authentication
MySQL: Database for storing bus, user, and booking data
Maven: Build tool
Lombok: For reducing boilerplate code
Spring Mail: For sending email notifications
JJWT: For JSON Web Token generation and validation

Prerequisites

Java 17: Ensure JDK 17 is installed (java --version).
Maven: Install Maven (mvn --version).
MySQL: MySQL 8.0 or later (mysql --version).
Git: For cloning and managing the repository (git --version).
IDE: IntelliJ IDEA, Eclipse, or VS Code (optional, for development).

Setup Instructions
1. Clone the Repository
Clone the repository and switch to the desired branch (e.g., admin):
git clone https://github.com/<your-username>/BusTicketBooking.git
cd BusTicketBooking
git checkout admin

2. Configure the Database
Create a MySQL database named Nexbus:
CREATE DATABASE Nexbus;

Update the src/main/resources/application.properties file with your database credentials:
spring.datasource.url=jdbc:mysql://localhost:3306/Nexbus
spring.datasource.username=root
spring.datasource.password=<your-password>

Note: Avoid committing sensitive data (e.g., passwords, JWT secrets) to Git. Use environment variables or a local properties file ignored by .gitignore.
3. Build the Project
Build the project using Maven:
mvn clean install

4. Run the Application
Start the Spring Boot application:
mvn spring-boot:run

The application will run on http://localhost:8090.
5. Verify Setup

Check the console logs for successful database connection and application startup.
Test API endpoints using tools like Postman or cURL (e.g., authentication or bus management endpoints, depending on the branch).

Project Structure
BusTicketBooking/
├── src/
│   ├── main/
│   │   ├── java/com/nexbus/BusTicketBooking/
│   │   │   ├── controller/    # REST controllers
│   │   │   ├── model/         # JPA entities (e.g., Bus, BusAmenity)
│   │   │   ├── repository/    # Spring Data JPA repositories
│   │   │   ├── service/       # Business logic
│   │   │   └── config/        # Security and other configurations
│   │   └── resources/
│   │       └── application.properties  # Configuration file
├── pom.xml                    # Maven dependencies
└── README.md                  # This file

Branch Details

main: Core functionality, shared across all roles. Merge stable changes here.
admin: Admin-specific backend logic, including bus and amenity management (e.g., BusAmenityRepository for linking buses to amenities).
bus operator: Features for bus operators, such as managing schedules and routes.
user: User-focused features, such as booking tickets and viewing bus details.

To work on a specific branch:
git checkout <branch-name>

Key Features (Admin Branch)

Bus Management: CRUD operations for buses.
Amenity Assignment: Link amenities to buses via BusAmenityRepository.
Authentication: JWT-based authentication for secure access.
Email Notifications: Send booking confirmations or alerts using Spring Mail.

Database Schema
Key tables (in the Nexbus database):

buses: Stores bus details (e.g., bus_id, primary key).
bus_amenities: Links buses to amenities (columns: id, bus_id, amenity_id).
Other tables: Users, bookings, schedules (depending on branch-specific features).

Verify the schema:
SHOW TABLES;
SHOW CREATE TABLE buses;
SHOW CREATE TABLE bus_amenities;

Configuration
The application.properties file configures the application:
spring.application.name=BusTicketBooking
server.port=8090
spring.datasource.url=jdbc:mysql://localhost:3306/Nexbus
spring.jpa.hibernate.ddl-auto=update
jwt.secret=<your-jwt-secret>
jwt.expiration=56400000
spring.jpa.show-sql=true
logging.level.org.springframework=DEBUG

Security Note: Store sensitive values (e.g., jwt.secret, database password) in environment variables or a secrets manager for production.
Troubleshooting

Application Fails to Start:
Check logs for errors (enabled via logging.level.org.springframework=DEBUG).
Verify database connection and schema.
Ensure pom.xml dependencies are correct (e.g., spring-boot-starter-data-jpa, mysql-connector-j).


Repository Errors:
Confirm entity mappings in Bus and BusAmenity (e.g., busID field in Bus).
Check BusAmenityRepository method names (e.g., findByBusBusIDAndAmenityId).


JWT Issues:
Ensure JWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson) are version 0.11.5.
Verify JWT secret and token generation logic.



Contributing

Fork the repository.
Create a feature branch (git checkout -b feature/<feature-name>).
Commit changes (git commit -m "Add feature").
Push to your branch (git push origin feature/<feature-name>).
Open a pull request to the appropriate branch (e.g., admin).

License
This project is licensed under the MIT License (pending formal license file).
Contact
For questions or support, contact the repository maintainer at prasannarps786@gmail.com or open an issue on GitHub.
