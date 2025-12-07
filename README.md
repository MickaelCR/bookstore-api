# Bookstore REST API

A comprehensive REST API for an online bookstore built with Spring Boot. This project implements a complete e-commerce backend with user authentication, book management, shopping cart, orders, reviews, and favorites functionality.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Deployment](#deployment)

## Features

- User registration and authentication with JWT
- Role-based access control (USER, ADMIN)
- Book catalog with categories, search, and filters
- Shopping cart management
- Order processing with status tracking
- Book reviews with like functionality
- Favorites/wishlist feature
- Pagination and sorting support
- Swagger/OpenAPI documentation
- RFC 9457 compliant error responses

## Technology Stack

- Java 21
- Spring Boot 3.3.5
- Spring Security with JWT
- Spring Data JPA
- MySQL 8.0
- Gradle
- Swagger/OpenAPI 3.0
- JUnit 5 / Mockito

## Getting Started

### Prerequisites

- Java 21 or higher
- MySQL 8.0 or higher
- Gradle 8.x

### Database Setup

Create a MySQL database:
```sql
CREATE DATABASE bookstore;
```

### Configuration

Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore?createDatabaseIfNotExist=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application will start at `http://localhost:8080/api`

### Default Accounts

After startup, the following accounts are available:

| Email | Password | Role |
|-------|----------|------|
| admin@bookstore.com | admin123 | ADMIN |
| kim.minjun@email.com | password123 | USER |

## API Documentation

Swagger UI is available at:
- http://localhost:8080/api/swagger-ui/index.html

OpenAPI specification:
- http://localhost:8080/api/v3/api-docs

## Database Schema

### Entities

- **User**: User accounts with roles (USER, ADMIN)
- **Category**: Book categories
- **Book**: Book information with category relationships
- **Review**: User reviews for books
- **ReviewLike**: Likes on reviews
- **Cart**: Shopping cart per user
- **CartItem**: Items in shopping cart
- **Order**: Customer orders
- **OrderItem**: Items in orders
- **Favorite**: User's favorite books

### Entity Relationships
```
User (1) --- (N) Review
User (1) --- (N) Order
User (1) --- (1) Cart
User (1) --- (N) Favorite

Book (N) --- (M) Category
Book (1) --- (N) Review
Book (1) --- (N) CartItem
Book (1) --- (N) OrderItem
Book (1) --- (N) Favorite

Cart (1) --- (N) CartItem
Order (1) --- (N) OrderItem
Review (1) --- (N) ReviewLike
```

## Authentication

The API uses JWT (JSON Web Token) for authentication.

### Login Flow

1. Register or login to receive access token and refresh token
2. Include the access token in the Authorization header for protected endpoints
3. Use the refresh token to obtain a new access token when expired

### Header Format
```
Authorization: Bearer <access_token>
```

### Token Expiration

- Access Token: 24 hours
- Refresh Token: 7 days

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /auth/register | Register a new user |
| POST | /auth/login | Login and get tokens |
| POST | /auth/refresh | Refresh access token |
| POST | /auth/logout | Logout (client-side) |

### Users (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /users/me | Get current user profile |
| PUT | /users/me | Update current user profile |

### Users - Admin (ADMIN only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /admin/users | Get all users (paginated) |
| GET | /admin/users/{id} | Get user by ID |
| PATCH | /admin/users/{id}/deactivate | Deactivate user |
| PATCH | /admin/users/{id}/activate | Activate user |

### Categories (Public read, ADMIN write)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /categories | Get all categories (paginated) |
| GET | /categories/all | Get all categories (no pagination) |
| GET | /categories/{id} | Get category by ID |
| POST | /categories | Create category (ADMIN) |
| PUT | /categories/{id} | Update category (ADMIN) |
| DELETE | /categories/{id} | Delete category (ADMIN) |

### Books (Public read, ADMIN write)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /books | Get all books with filters |
| GET | /books/top | Get top 10 books by views |
| GET | /books/{id} | Get book by ID |
| POST | /books | Create book (ADMIN) |
| PUT | /books/{id} | Update book (ADMIN) |
| DELETE | /books/{id} | Delete book (ADMIN) |

Query parameters for GET /books:
- `keyword`: Search in title and author
- `categoryId`: Filter by category
- `minPrice`: Minimum price filter
- `maxPrice`: Maximum price filter
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)
- `sort`: Sort field and direction

### Reviews (Public read, Authenticated write)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /reviews/book/{bookId} | Get reviews for a book |
| GET | /reviews/user/{userId} | Get reviews by user |
| GET | /reviews/me | Get my reviews |
| GET | /reviews/{id} | Get review by ID |
| POST | /reviews | Create review |
| PUT | /reviews/{id} | Update review |
| DELETE | /reviews/{id} | Delete review |
| POST | /reviews/{id}/like | Like a review |
| DELETE | /reviews/{id}/like | Unlike a review |

### Cart (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /cart | Get current user's cart |
| POST | /cart/items | Add item to cart |
| PUT | /cart/items/{itemId} | Update cart item quantity |
| DELETE | /cart/items/{itemId} | Remove item from cart |
| DELETE | /cart | Clear cart |

### Orders (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /orders | Get my orders |
| GET | /orders/{id} | Get order by ID |
| POST | /orders | Create order from cart |
| PATCH | /orders/{id}/cancel | Cancel order |

### Orders - Admin (ADMIN only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /admin/orders | Get all orders |
| GET | /admin/orders/{id} | Get order by ID |
| PATCH | /admin/orders/{id}/status | Update order status |

### Favorites (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /favorites | Get my favorites |
| POST | /favorites/{bookId} | Add to favorites |
| DELETE | /favorites/{bookId} | Remove from favorites |
| GET | /favorites/{bookId}/check | Check if book is favorited |

### Health and Statistics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /health | Health check (Public) |
| GET | /admin/stats/summary | Get statistics (ADMIN) |

## Error Handling

The API returns errors in RFC 9457 Problem Details format:
```json
{
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Book not found with id: 999",
    "instance": "/api/books/999",
    "timestamp": "2024-12-06T12:00:00Z",
    "requestId": "abc-123-def"
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request / Validation Error |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 405 | Method Not Allowed |
| 409 | Conflict |
| 500 | Internal Server Error |

## Testing

Run tests with:
```bash
./gradlew test
```

Test coverage includes:
- Unit tests for services
- Integration tests for controllers
- 32 test cases total

## Deployment

### Build JAR
```bash
./gradlew bootJar
```

The JAR file will be created at `build/libs/bookstore-0.0.1-SNAPSHOT.jar`

### Run JAR
```bash
java -jar build/libs/bookstore-0.0.1-SNAPSHOT.jar
```

### JCloud Deployment

1. Upload the JAR file to JCloud server
2. Configure environment variables for database connection
3. Run with nohup for persistence:
```bash
nohup java -jar bookstore-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

## Project Structure
```
src/main/java/kr/ac/jbnu/cr/bookstore/
├── config/
│   ├── DataSeeder.java
│   └── SecurityConfig.java
├── controller/
│   ├── AdminOrderController.java
│   ├── AdminStatsController.java
│   ├── AdminUserController.java
│   ├── AuthController.java
│   ├── BookController.java
│   ├── CartController.java
│   ├── CategoryController.java
│   ├── FavoriteController.java
│   ├── HealthController.java
│   ├── OrderController.java
│   ├── ReviewController.java
│   └── UserController.java
├── dto/
│   ├── request/
│   └── response/
├── exception/
│   ├── BadRequestException.java
│   ├── DuplicateResourceException.java
│   ├── ForbiddenException.java
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── StateConflictException.java
│   └── UnauthorizedException.java
├── model/
│   ├── Book.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Category.java
│   ├── Favorite.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderStatus.java
│   ├── Review.java
│   ├── ReviewLike.java
│   ├── Role.java
│   └── User.java
├── repository/
├── security/
│   ├── JwtAuthentication.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
├── service/
└── BookstoreApplication.java
```

## Author

Rakotoarison Christian - Jeonbuk National University

## License

This project is for educational purposes as part of the Web Service course at JBNU.