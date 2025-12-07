# Bookstore REST API

REST API for an online bookstore built with Spring Boot.

## Tech Stack

Java 21 | Spring Boot 3.3.5 | Spring Security + JWT | MySQL 8.0 | Gradle

## Quick Start

### Prerequisites

- Java 21+
- MySQL 8.0+

### Setup
```bash
# Create database
mysql -u root -p -e "CREATE DATABASE bookstore;"

# Create .env file (see .env.example)
cp .env.example .env

# Run
./gradlew bootRun
```

Application starts at `http://localhost:8080/api`

### Default Accounts

| Email | Password | Role |
|-------|----------|------|
| admin@bookstore.com | admin123 | ADMIN |
| kim.minjun@email.com | password123 | USER |

## Documentation

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/api/swagger-ui/index.html |
| OpenAPI spec | http://localhost:8080/api/v3/api-docs |
| API Design | [docs/api-design.md](docs/api-design.md) |
| DB Schema | [docs/db-schema.md](docs/db-schema.md) |
| Architecture | [docs/architecture.md](docs/architecture.md) |

## Security Features

### CORS Configuration

Allowed origins for API access:
- `http://localhost:3000`
- `http://localhost:8080`
- `http://127.0.0.1:8080`

### Rate Limiting

- **Limit**: 100 requests per minute per IP address
- **Response when exceeded**: HTTP 429 Too Many Requests
- **Headers included in all responses**:
    - `RateLimit-Limit`: Maximum requests allowed
    - `RateLimit-Remaining`: Requests remaining in current window
    - `RateLimit-Reset`: Unix timestamp when the limit resets

### Password Security

All passwords are hashed using BCrypt before storage.

## Authentication Flow
```
┌─────────┐                              ┌─────────┐
│  Client │                              │   API   │
└────┬────┘                              └────┬────┘
     │                                        │
     │  1. POST /auth/login                   │
     │    {email, password}                   │
     │───────────────────────────────────────>│
     │                                        │
     │  2. Return tokens                      │
     │    {accessToken, refreshToken}         │
     │<───────────────────────────────────────│
     │                                        │
     │  3. Request with token                 │
     │    Authorization: Bearer <accessToken> │
     │───────────────────────────────────────>│
     │                                        │
     │  4. Protected resource                 │
     │<───────────────────────────────────────│
     │                                        │
     │  5. Token expired (401)                │
     │<───────────────────────────────────────│
     │                                        │
     │  6. POST /auth/refresh                 │
     │    {refreshToken}                      │
     │───────────────────────────────────────>│
     │                                        │
     │  7. New access token                   │
     │<───────────────────────────────────────│
     │                                        │
```

Token validity: Access Token (24h) | Refresh Token (7 days)

## Authorization Matrix

| Endpoint | Public | USER | ADMIN |
|----------|--------|------|-------|
| POST /auth/** | Yes | Yes | Yes |
| GET /health | Yes | Yes | Yes |
| GET /books/** | Yes | Yes | Yes |
| GET /categories/** | Yes | Yes | Yes |
| GET /reviews/** | Yes | Yes | Yes |
| POST/PUT/DELETE /books/** | No | No | Yes |
| POST/PUT/DELETE /categories/** | No | No | Yes |
| GET/PUT /users/me | No | Yes | Yes |
| /cart/** | No | Yes | Yes |
| /orders/** | No | Yes | Yes |
| /favorites/** | No | Yes | Yes |
| POST/PUT/DELETE /reviews/** | No | Yes | Yes |
| /admin/** | No | No | Yes |

## Database Connection
```
jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

## Performance Considerations

- **Pagination**: All list endpoints support pagination (default: 20 items per page)
- **Database indexes**: Applied on frequently queried columns (email, ISBN, foreign keys)
- **Connection pooling**: HikariCP with default settings
- **Stateless authentication**: JWT tokens eliminate server-side session storage

## Testing
```bash
./gradlew test
```

## Deployment
```bash
# Build JAR
./gradlew bootJar

# Run
java -jar build/libs/bookstore-0.0.1-SNAPSHOT.jar
```

## Author

Rakotoarison Christian - Jeonbuk National University