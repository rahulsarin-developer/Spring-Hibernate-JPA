# Spring Hibernate JPA - REST API

A production-oriented REST API application developed using Java,
Spring Boot, Spring Data JPA, Hibernate, and MySQL.

This project demonstrates enterprise-level application architecture,
database operations, RESTful API development, pagination, sorting,
filtering, exception handling, API documentation, and automated testing.

## Technology Stack

- Java
- Spring Boot
- Jakarta Persistence API (JPA)
- Hibernate ORM
- EntityManager
- JPQL (Java Persistence Query Language)
- MySQL
- Maven
- Swagger / OpenAPI
- JUnit 5
- Mockito
- Git

## Key Features

- RESTful API development
- CRUD operations
- Layered application architecture
- Controller, Service and ServiceImpl implementation
- Repository and custom RepositoryImpl implementation
- Entity and DTO separation
- Request validation
- Global exception handling
- Pagination and sorting
- Dynamic filtering
- Database transaction management
- Swagger / OpenAPI documentation
- Unit and integration testing
- Clean and maintainable code

## Project Structure

```text
Spring-Hibernate-JPA/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.project.spring_hibernate_jpa/
│   │   │       ├── controller/
│   │   │       │   └── EmployeeController.java
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── SpringHibernateJpaApplication.java
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       │       └── V1__create_employees.sql
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.yml
│   └── test/
├── pom.xml
├── .gitignore
└── README.md
```

## Layer Responsibilities

| Layer | Responsibility |
|-------|----------------|
| Controller | Handles HTTP requests and responses |
| Service | Defines business operations |
| ServiceImpl | Implements business logic |
| Repository | Performs database operations |
| RepositoryImpl | Implements custom database queries |
| Model | Represents database entities |
| DTO | Transfers data between application layers |
| Config | Manages application configurations |
| Exception | Handles application exceptions |

## REST API Endpoints

Base URL: `/api/v1/employee`

| HTTP Method | Endpoint | Description |
|-------------|----------|-------------|
| POST | /api/v1/employee/add | Create an employee |
| GET | /api/v1/employee/fetch/{id} | Retrieve employee by ID |
| PUT | /api/v1/employee/update/{id} | Update employee details |
| DELETE | /api/v1/employee/delete/{id} | Delete an employee |
| PATCH | /api/v1/employee/update/{id}/salary | Update employee salary |
| GET | /api/v1/employee/search | Search, filter, paginate and sort employees |
| GET | /api/v1/employee/count | Count employees with optional department filter |

## API Versioning

The application implements URI-based API versioning to
support backward compatibility and future API enhancements.

## Database Configuration

Create a MySQL database:

```sql
CREATE DATABASE employee_db;
```

Configure the following properties in
`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

Set the database username and password using environment
variables before starting the application.

## Prerequisites

Ensure the following software is installed:

- Java Development Kit (compatible with pom.xml)
- Apache Maven
- MySQL Server
- Git
- IntelliJ IDEA, Eclipse or Spring Tool Suite
- Postman (optional)

## Build and Run

### Build the application

```bash
mvn clean install
```

### Run the application

```bash
mvn spring-boot:run
```

The application will be available at:

http://localhost:8080

## Swagger / OpenAPI Documentation

Swagger provides interactive API documentation and allows
developers to test REST API endpoints directly from the browser.

### Swagger UI

http://localhost:8080/swagger-ui/index.html

### OpenAPI Specification

http://localhost:8080/v3/api-docs

## Exception Handling

The application implements centralized exception handling
using GlobalExceptionHandler to provide consistent and
meaningful API error responses.

### Exception Classes

| Exception Class | Responsibility |
|-----------------|----------------|
| GlobalExceptionHandler | Centralized handling of application exceptions |
| ResourceNotFoundException | Handles scenarios where an employee or resource is not found |
| ConflictException | Handles resource or data conflict scenarios |

## HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 OK | Request completed successfully |
| 201 Created | Resource created successfully |
| 204 No Content | Request completed without response body |
| 400 Bad Request | Invalid request |
| 404 Not Found | Requested resource not found |
| 409 Conflict | Resource or data conflict |
| 500 Internal Server Error | Unexpected server error |

## Testing

The project uses JUnit 5 and Mockito for automated testing.

### Unit Testing

- Controller layer testing
- Service layer testing
- Business logic validation
- Exception scenario testing
- Mocking repository dependencies

### Integration Testing

- REST API integration testing
- Repository layer testing
- Database interaction testing
- Spring Boot application context testing

### Execute Tests

```bash
mvn test
```
