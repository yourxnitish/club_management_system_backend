# College Club Management System — Backend API

Enterprise-grade university portal REST API built with **Spring Boot 3.3.5**, **Spring Security 6**, and **Spring Data JPA**.

## 🚀 Features

- **JWT Authentication:** Stateless HMAC-SHA256 tokens with rotating refresh tokens and BCrypt password encryption.
- **Role-Based Access Control:** Strict authorization across `STUDENT`, `CLUB_ADMIN`, and `SYSTEM_ADMIN`.
- **Database Migrations:** Flyway schema migrations with seed data for instant development.
- **Dual Persistence Modes:** Supports both MySQL 8.4 in production/Docker and embedded/file H2 for instant local development.
- **Automatic Seeding:** Pre-populates administrative, club officer, and student test accounts on initial launch.
- **OpenAPI & Swagger Documentation:** Interactive API testing at `/swagger-ui.html`.
- **Monitoring & Telemetry:** Spring Boot Actuator health and metrics endpoints at `/actuator/health`.

## 🛠️ Tech Stack

- **Java Version:** Java 17 / 21
- **Framework:** Spring Boot 3.3.5
- **Security:** Spring Security, JJWT 0.12.6
- **Data & ORM:** Spring Data JPA, Hibernate ORM
- **Database Migrations:** Flyway 10.x
- **Documentation:** Springdoc OpenAPI 2.6.0
- **Testing:** JUnit 5, Mockito, MockMvc

## 📦 Getting Started

### Prerequisites
- JDK 17 or JDK 21 installed (`java -version`)
- Maven 3.8+ installed (`mvn -version`)

### Run Locally (Offline / Instant Mode with H2)
```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=local
```
Runs at [http://localhost:8080](http://localhost:8080) and auto-generates sample clubs, events, and users.

### Run with MySQL
1. Ensure MySQL is running on port 3306 with database `college_club_db`.
2. Launch the backend:
```bash
mvn clean spring-boot:run
```

### Run Tests
```bash
mvn test
```

## 🔑 Pre-Configured Test Credentials

| Role | Email | Password | Assigned Scope |
|---|---|---|---|
| **System Admin** | `admin@collegeclub.edu` | `Admin@123456` | Platform administration, audit logs |
| **Club Admin (Robotics)** | `robotics.lead@collegeclub.edu` | `Admin@123456` | Robotics Society roster & events |
| **Student** | `alex.student@collegeclub.edu` | `Student@123456` | General student account |

## 📄 License
MIT
