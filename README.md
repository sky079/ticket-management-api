# Ticket Management REST API

A backend REST API built with Spring Boot that manages support tickets
from creation to resolution with SLA tracking.

## Tech Stack
- Java 17
- Spring Boot 4
- Spring Security + JWT
- PostgreSQL
- Maven

## Features
- User registration and login with JWT authentication
- Create, assign and resolve support tickets
- Automatic SLA calculation based on priority
    - HIGH priority: 4 hours
    - MEDIUM priority: 24 hours
    - LOW priority: 72 hours
- SLA breach detection
- Report endpoint showing open, resolved and breached tickets

## API Endpoints

### Auth
POST /api/auth/register  - Register new user
POST /api/auth/login     - Login and get JWT token

### Tickets
POST   /api/tickets              - Create ticket
GET    /api/tickets              - Get all tickets
GET    /api/tickets/{id}         - Get ticket by id
PUT    /api/tickets/{id}/assign  - Assign ticket to agent
PUT    /api/tickets/{id}/resolve - Resolve ticket + SLA check
GET    /api/tickets/report       - Get ticket statistics

## Setup
1. Install Java 17 and PostgreSQL
2. Create database: ticket_db
3. Update application.properties with your DB password
4. Run: mvn spring-boot:run
5. API available at: http://localhost:8080