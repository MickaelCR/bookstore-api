# Bookstore REST API

REST API for an online bookstore built with Spring Boot.

## Deployment Info (JCloud)

The application is deployed and accessible online:

| Resource | URL |
|----------|-----|
| **Swagger UI** | http://113.198.66.75:10148/api/swagger-ui/index.html |
| **API Base URL** | http://113.198.66.75:10148/api |
| **Health Check** | http://113.198.66.75:10148/api/health |

> **Note**: The API is running on port `10148`.

## Tech Stack

Java 21 | Spring Boot 3.3.5 | Spring Security + JWT | MySQL 8.0 | Gradle | Docker

## Documentation & Resources

The `docs/` folder contains detailed design documents reflecting the implementation:

| Resource | URL | Description |
|----------|-----|-------------|
| OpenAPI spec | http://localhost:8080/api/v3/api-docs | Auto-generated spec |
| API Design | [docs/api-design.md](docs/api-design.md) | **Full list of endpoints**, methods, and descriptions |
| DB Schema | [docs/db-schema.md](docs/db-schema.md) | Database schema and ERD |
| Architecture | [docs/architecture.md](docs/architecture.md) | System architecture overview |

## Installation & Execution

### Prerequisites
- Java 21+
- MySQL 8.0+
- Docker & Docker Compose (Optional)

### Environment Variables
Create a `.env` file in the root directory (based on `.env.example`) with your own secure values:

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | MySQL Connection URL | `jdbc:mysql://localhost:3306/bookstore?useSSL=false...` |
| `DB_USERNAME` | Database username | `your_db_username` |
| `DB_PASSWORD` | Database password | `your_db_password` |
| `JWT_SECRET` | Secret key for signing tokens | `your_secure_random_secret_key_min_32_chars` |
| `JWT_EXPIRATION` | Access token validity (ms) | `86400000` (24h) |
| `JWT_REFRESH_EXPIRATION` | Refresh token validity (ms) | `604800000` (7 days) |
| `SERVER_PORT` | Port for the API server | `8080` |

### Option 1: Docker Execution (Recommended)
This command handles dependency installation, database creation, migration, seeding, and server startup automatically.

```bash
# Build and start containers
docker-compose up --build
````

The application will start at `http://localhost:8080/api`.

### Option 2: Manual Local Execution

```bash
# 1. Create database
mysql -u <your_db_username> -p -e "CREATE DATABASE bookstore;"

# 2. Configure environment
cp .env.example .env
# (Edit .env with your actual database credentials)

# 3. Run the application
# Note: Database migration (Hibernate) and Data Seeding are automatic on startup.
./gradlew bootRun
```

### Testing

To run the automated test suite (Unit & Integration tests):

```bash
./gradlew test
```

## Default Accounts (Dev/Test Only)

The application automatically seeds default users (Admin and User) for testing purposes during the first startup.

> **Security Note:** Specific credentials for these accounts are **not documented here** to prevent sensitive data exposure.
> Please refer to the `DataSeeder.java` file in the source code or the private credentials file submitted via Classroom.

## API & Security Features

### Authentication Flow

Token validity: Access Token (24h) | Refresh Token (7 days)

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
```

### Authorization Matrix

| Endpoint | Public | USER | ADMIN |
|----------|--------|------|-------|
| POST /auth/\*\* | Yes | Yes | Yes |
| GET /health | Yes | Yes | Yes |
| GET /books/\*\* | Yes | Yes | Yes |
| GET /categories/\*\* | Yes | Yes | Yes |
| GET /reviews/\*\* | Yes | Yes | Yes |
| POST/PUT/DELETE /books/\*\* | No | No | Yes |
| POST/PUT/DELETE /categories/\*\* | No | No | Yes |
| GET/PUT /users/me | No | Yes | Yes |
| /cart/\*\* | No | Yes | Yes |
| /orders/\*\* | No | Yes | Yes |
| /favorites/\*\* | No | Yes | Yes |
| POST/PUT/DELETE /reviews/\*\* | No | Yes | Yes |
| /admin/\*\* | No | No | Yes |

*For a detailed list of all 30+ endpoints with descriptions, please refer to [docs/api-design.md](https://www.google.com/search?q=docs/api-design.md).*

### Security Measures

* **CORS**: Allowed origins configured for local development (`localhost:3000`, `8080`, `127.0.0.1`).
* **Rate Limiting**: Max 100 requests/minute per IP. Headers included: `RateLimit-Limit`, `RateLimit-Remaining`.
* **Password Security**: BCrypt hashing for all user passwords.

## Architecture & Performance

* **Pagination**: All list endpoints support pagination (default: 20 items per page).
* **Database Indexes**: Applied on frequently queried columns (email, ISBN, foreign keys) to optimize search and joins.
* **Connection Pooling**: HikariCP is used for efficient database connection management.
* **N+1 Prevention**: JPA Entity Graphs are used to fetch related entities efficiently.

## Limitations & Future Improvements

To meet the submission deadline and facilitate development, several features defined in the original design (Assignment 1) were simplified or deferred.

### Architecture Simplifications (vs. Assignment 1)

- **Author Management**: In the original ERD, `Authors` was designed as a separate entity with a Many-to-Many relationship (via `book_authors`). In this implementation, it was simplified to a direct `String` field in the `Book` entity.
- **Marketplace Logic**: The initial design included `Sellers` and `Settlements` tables for a multi-vendor marketplace. This was simplified to a standard B2C bookstore model managed by a single Admin.
- **Comments System**: The detailed `Comments` entity (supporting threaded replies on books and reviews) was omitted in favor of a simpler `Review` only system.
- **Promotions**: The `Coupons` and `Discounts` entities defined in the database schema were deferred for future iterations.
- **Payment & Shipping**: Detailed `Payments` and `Shipments` tables were consolidated into the `Order` entity's status flow (CREATED -\> PAID -\> SHIPPED) to reduce complexity.

### Technical Limitations

- **Image Storage**: Book covers are not currently supported. Future versions should implement object storage (AWS S3/MinIO).
- **Search Engine**: Search is currently performed via SQL `LIKE` queries. Integration with Elasticsearch is planned for better performance on large datasets.

### Future Improvements

- **Restore Full Schema**: Implement the `Authors`, `Coupons`, and `Sellers` entities to match the original ERD.
- **Notification System**: Add email/push notifications for order status updates.
- **Library vs. Wishlist**: Separate the "Favorites" feature into a distinct "Wishlist" (future purchase) and "Library" (owned content) as originally planned.

## Author

Rakotoarison Christian - Jeonbuk National University