# Architecture - Bookstore API

## Technology Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.3.5 |
| Security | Spring Security + JWT |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Documentation | SpringDoc OpenAPI 3.0 |
| Build Tool | Gradle |
| Testing | JUnit 5, Mockito |

## Project Structure
```
src/main/java/kr/ac/jbnu/cr/bookstore/
├── config/
│   ├── DataSeeder.java
│   ├── OpenApiConfig.java
│   ├── RateLimitingFilter.java
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

## Layered Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                      Client (Postman, Browser)               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Security Filters                          │
│  ┌─────────────────┐  ┌─────────────────────────────────┐   │
│  │ RateLimitFilter │  │   JwtAuthenticationFilter       │   │
│  └─────────────────┘  └─────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Controller Layer                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │   Auth   │ │   Book   │ │   Cart   │ │  Order   │  ...  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Service Layer                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │AuthSvc   │ │ BookSvc  │ │ CartSvc  │ │ OrderSvc │  ...  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                           │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ UserRepo │ │ BookRepo │ │ CartRepo │ │OrderRepo │  ...  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      MySQL Database                          │
└─────────────────────────────────────────────────────────────┘
```

## Security Flow
```
Request → RateLimitFilter → JwtAuthFilter → Controller
                                  │
                                  ▼
                          ┌──────────────┐
                          │ Extract JWT  │
                          │ from Header  │
                          └──────────────┘
                                  │
                                  ▼
                          ┌──────────────┐
                          │ Validate &   │
                          │ Parse Token  │
                          └──────────────┘
                                  │
                                  ▼
                          ┌──────────────┐
                          │ Set Security │
                          │   Context    │
                          └──────────────┘
```

## Error Handling

All exceptions are handled by `GlobalExceptionHandler` and return RFC 9457 Problem Details format:
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

## Rate Limiting

- 100 requests per minute per IP address
- Returns 429 Too Many Requests when exceeded
- Headers: RateLimit-Limit, RateLimit-Remaining, RateLimit-Reset

## CORS Configuration

Allowed origins:
- http://localhost:3000
- http://localhost:8080
- http://127.0.0.1:8080

## Design Decisions

Some simplifications were made from the initial API specification:
- Comments system: Deferred to future iteration
- Wishlists/Library: Consolidated into Favorites feature
- Coupons/Discounts: Out of scope for MVP