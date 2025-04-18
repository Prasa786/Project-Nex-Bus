

```markdown
# BusTicketBooking Backend

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen)
![Java](https://img.shields.io/badge/Java-17-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)

## Overview

The BusTicketBooking backend is a Spring Boot application that powers a bus reservation system with:

- 🚍 Bus and route management
- 🛎️ Amenity assignments
- 🔐 JWT-based authentication
- ✉️ Email notifications
- 📊 Multi-role access (Admin, Operator, User)

## Branches

| Branch       | Description                          |
|--------------|--------------------------------------|
| `main`       | Core stable functionality            |
| `admin`      | Admin features (bus/amenity mgmt)    |
| `busoperator`| Operator features (schedules/routes) |
| `user`       | User features (booking/search)       |

## Tech Stack

- **Backend**: Spring Boot 3.4.4
- **Database**: MySQL 8.0+
- **Security**: JWT Authentication
- **Build**: Maven
- **Email**: Spring Mail

## Getting Started

### Prerequisites

- Java 17 JDK
- MySQL 8.0+
- Maven 3.6+
- Git

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/<your-username>/BusTicketBooking.git
   cd BusTicketBooking
   ```

2. **Create MySQL database**:
   ```sql
   CREATE DATABASE Nexbus;
   ```

3. **Configure application**:
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/Nexbus
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build and run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The application will start at `http://localhost:8090`

## Project Structure

```
src/
├── main/
│   ├── java/com/nexbus/BusTicketBooking/
│   │   ├── config/       # Security & app config
│   │   ├── controller/    # API endpoints
│   │   ├── dto/          # Data transfer objects
│   │   ├── exception/    # Custom exceptions
│   │   ├── model/        # JPA entities
│   │   ├── repository/   # Data access layer
│   │   ├── service/      # Business logic
│   │   └── util/        # Helpers & utilities
│   └── resources/       # Config files
```

## Key Features

### Admin Branch Features
- Bus CRUD operations
- Amenity management
- User management
- System configuration

### Security
- JWT authentication
- Role-based access control
- Password encryption

### Notifications
- Booking confirmations
- Payment receipts
- Schedule changes

## API Documentation

Explore endpoints using Swagger UI after running the app:
`http://localhost:8090/swagger-ui.html`

## Troubleshooting

**Common Issues**:

1. **Database connection fails**:
   - Verify MySQL credentials
   - Check if MySQL service is running

2. **JWT errors**:
   - Ensure consistent secret key
   - Validate token expiration settings

3. **Email failures**:
   - Configure SMTP properties
   - Check spam folder

## Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact

Prasanna R - prasannarps786@gmail.com

Project Link: [https://github.com/Prasa786/BusTicketBooking](https://github.com/Prasa786/BusTicketBooking)
```

### Key Improvements:
1. **Visual Hierarchy**: Added badges and clear section headers
2. **Branch Table**: Organized branch information for quick reference
3. **Concise Setup**: Simplified installation steps
4. **Troubleshooting**: Added common solutions
5. **API Docs**: Mentioned Swagger UI access
6. **Structure Visualization**: Clean directory tree display
7. **Mobile-Friendly**: Proper Markdown formatting
