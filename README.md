# Ticket Management REST API

A production-grade backend REST API built with Spring Boot that manages support tickets
from creation to resolution with automatic SLA tracking and JWT authentication.

## Tech Stack
- Java 17
- Spring Boot 4
- Spring Security + JWT
- PostgreSQL
- Maven
- Docker

## Features
- User registration and login with JWT authentication
- Role based access control — ADMIN, AGENT, USER
- Create, assign and resolve support tickets
- Automatic SLA calculation based on priority
  - HIGH priority: 4 hours max
  - MEDIUM priority: 24 hours max
  - LOW priority: 72 hours max
- Automatic SLA breach detection with minute-level precision
- Paginated ticket listing
- Report endpoint showing open, resolved and breached counts
- Global exception handling with proper HTTP status codes
- Input validation on all endpoints
- Interactive API documentation with Swagger UI
- Fully containerized with Docker

## API Endpoints

### Auth
- POST /api/auth/register — Register new user (ADMIN, AGENT, USER roles)
- POST /api/auth/login — Login and receive JWT token

### Tickets
- POST /api/tickets — Create ticket (any role)
- GET /api/tickets?page=0&size=10 — Get paginated tickets (any role)
- GET /api/tickets/{id} — Get ticket by id (any role)
- PUT /api/tickets/{id}/assign?agentId={id} — Assign ticket (ADMIN only)
- PUT /api/tickets/{id}/resolve — Resolve ticket and trigger SLA (AGENT, ADMIN)
- GET /api/tickets/report — Get ticket statistics (ADMIN only)

## Running with Docker (Recommended)

Prerequisites: Docker Desktop installed

1. Clone the repository
   git clone https://github.com/sky079/ticket-management-api.git

2. Navigate to project folder
   cd ticket-management-api

3. Start the application
   docker compose up

4. Access the API
   Swagger UI: http://localhost:8080/swagger-ui.html

Everything starts automatically — no Java or PostgreSQL installation needed.

## Running Locally

Prerequisites: Java 17, Maven, PostgreSQL

1. Clone the repository
   git clone https://github.com/sky079/ticket-management-api.git

2. Create database in PostgreSQL
   CREATE DATABASE ticket_db;

3. Copy the example properties file
   cp src/main/resources/application.properties.example src/main/resources/application.properties

4. Update application.properties with your database password and port

5. Run the application
   mvn spring-boot:run

6. Access the API
   Swagger UI: http://localhost:8080/swagger-ui.html

## Running Tests
mvn test

## Project Structure
src/main/java/com/ticket/ticketmanagement/
├── config/       — App and Swagger configuration
├── controller/   — REST API endpoints
├── dto/          — Data transfer objects
├── entity/       — Database entities
├── exception/    — Custom exceptions and global handler
├── repository/   — Database repositories
├── security/     — JWT filter and security config
└── service/      — Business logic and SLA engine

## Authentication
All endpoints except /api/auth/** require a JWT token in the Authorization header:
Authorization: Bearer your_jwt_token_here

Get a token by calling POST /api/auth/login with your credentials.