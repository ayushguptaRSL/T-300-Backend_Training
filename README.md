# Leave Management System (backendTraining)

## Project Overview

A Spring Boot RESTful API service for managing employee leave requests. The application provides endpoints to create, retrieve, update, and delete leave applications with in-memory persistence and input validation.

### Key Features
- **Leave Lifecycle**: Create leave requests (assigned `PENDING` by default), update details or status (`APPROVED`, `REJECTED`), and cancel/delete requests.
- **Leave Types**: Supports `PAID_LEAVE`, `UNPAID_LEAVE`, and `CAUSAL_LEAVE`.
- **Validation**:
  - Mandatory fields (`employeeId`, `leaveType`, `startDate`, `endDate`, `reason`).
  - Past dates are disallowed (`@FutureOrPresent`).
  - Logical validation ensuring `startDate` is before or equal to `endDate`.
- **In-Memory Store**: Thread-safe in-memory map repository with atomic ID sequence generation.
- **Global Error Handling**: Standardized error responses for bad requests (`400`), not found errors (`404`), and invalid date ranges (`422`).
- **Tech Stack**: Java 17, Spring Boot, Spring Web, Jakarta Bean Validation, Lombok, JUnit 5, Mockito.

---

## Build and Run Instructions

### Prerequisites
- JDK 17 or higher
- Maven (or use the bundled Maven wrapper)

### Build the Project
Compile and package the application into a JAR:

- **Windows**:
  ```cmd
  .\mvnw.cmd clean package
  ```
- **Linux / macOS**:
  ```bash
  ./mvnw clean package
  ```

### Run the Application
- **Using Maven Wrapper**:
  - Windows: `.\mvnw.cmd spring-boot:run`
  - Linux / macOS: `./mvnw spring-boot:run`
- **Using Executable JAR**:
  ```bash
  java -jar target/backendTraining-0.0.1-SNAPSHOT.jar
  ```

The service starts on port **8080** under the default **dev** profile (`http://localhost:8080`).

---

## API Endpoint Summary

**Base URL**: `/v1/api/leaves`

| Method | Endpoint | Description | Request Body | Status Codes |
|---|---|---|---|---|
| `GET` | `/v1/api/leaves` | Get all leave requests | None | `200 OK` |
| `GET` | `/v1/api/leaves/{id}` | Get leave request by ID | None | `200 OK`, `404 Not Found` |
| `POST` | `/v1/api/leaves` | Create a new leave request | `LeaveRequestDTO` (JSON) | `201 Created`, `400 Bad Request`, `422 Unprocessable Content` |
| `PUT` | `/v1/api/leaves/{id}` | Update an existing leave request | `UpdateLeaveRequestDTO` (JSON) | `200 OK`, `404 Not Found`, `422 Unprocessable Content` |
| `DELETE` | `/v1/api/leaves/{id}` | Delete a leave request by ID | None | `200 OK`, `404 Not Found` |

### Request Fields

- **Create Request (`LeaveRequestDTO`)**:
  - `employeeId` (Long, Required): ID of the employee.
  - `leaveType` (Enum, Required): `PAID_LEAVE`, `UNPAID_LEAVE`, `CAUSAL_LEAVE`.
  - `startDate` (LocalDate `YYYY-MM-DD`, Required): Present or future date.
  - `endDate` (LocalDate `YYYY-MM-DD`, Required): Present or future date, on or after `startDate`.
  - `reason` (String, Required): Reason for the leave.

- **Update Request (`UpdateLeaveRequestDTO`)**:
  - All fields are optional: `leaveType`, `startDate`, `endDate`, `reason`, `status` (`PENDING`, `APPROVED`, `REJECTED`).

---

## Testing Instructions

The test suite covers unit tests for the service layer, MockMvc tests for the REST controller, and Spring Boot context verification.

### Run All Tests
- **Windows**:
  ```cmd
  .\mvnw.cmd test
  ```
- **Linux / macOS**:
  ```bash
  ./mvnw test
  ```

### Run Individual Test Classes
- **Service Layer Tests**: `.\mvnw.cmd test -Dtest=LeaveServiceTest`
- **Controller Layer Tests**: `.\mvnw.cmd test -Dtest=LeaveControllerTest`
- **Context Integration Test**: `.\mvnw.cmd test -Dtest=BackendTrainingApplicationTests`

Test reports are generated in `target/surefire-reports/`.
