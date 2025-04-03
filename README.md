# Calendar Service

A microservice that manages user availability and scheduling for the Community Service Exchange Platform.

## Features

- Manage user availability schedules
- Track recurring availability patterns
- Schedule and confirm service appointments
- Check for scheduling conflicts
- Generate available time slots

## Technology Stack

- Spring Boot 3.x
- Spring Data JPA
- Spring Web
- PostgreSQL
- ModelMapper

## Getting Started

### Prerequisites

- Java 21
- Maven
- PostgreSQL

### Installation

1. Clone the repository
2. Configure the database settings in `application.properties`
3. Run the application with Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Endpoints

### User Calendar

- `POST /api/calendars?userId={userId}` - Create a user calendar
- `GET /api/calendars/{userId}` - Get a user's calendar
- `GET /api/calendars/{userId}/available-slots?start={start}&end={end}` - Get available time slots
- `DELETE /api/calendars/{userId}` - Delete a user calendar

### Availability

- `POST /api/availabilities` - Create an availability record
- `GET /api/availabilities/{id}` - Get an availability by ID
- `GET /api/availabilities/user-calendar/{userCalendarId}` - Get availabilities by user calendar
- `GET /api/availabilities/user-calendar/{userCalendarId}/day/{dayOfWeek}` - Get availabilities by day
- `PUT /api/availabilities/{id}` - Update an availability
- `DELETE /api/availabilities/{id}` - Delete an availability

### Scheduled Slots

- `POST /api/scheduled-slots` - Create a scheduled slot
- `GET /api/scheduled-slots/{id}` - Get a scheduled slot by ID
- `GET /api/scheduled-slots/exchange/{exchangeId}` - Get a scheduled slot by exchange ID
- `GET /api/scheduled-slots/user/{userId}` - Get scheduled slots by user
- `GET /api/scheduled-slots/user/{userId}/date-range?start={start}&end={end}` - Get scheduled slots by date range
- `PUT /api/scheduled-slots/{id}` - Update a scheduled slot
- `PUT /api/scheduled-slots/{id}/confirm` - Confirm a scheduled slot
- `DELETE /api/scheduled-slots/{id}` - Delete a scheduled slot

## Development

### Running Tests

```bash
./mvnw test
```

### Building the Application

```bash
./mvnw clean package
```