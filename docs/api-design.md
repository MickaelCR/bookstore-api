# API Design - Bookstore REST API

## Overview

This API provides a complete backend for an online bookstore with user authentication, book management, shopping cart, orders, reviews, and favorites functionality.

## Base URL
```
http://<server>:<port>/api
```

## Authentication

- JWT-based authentication
- Access Token: 24 hours validity
- Refresh Token: 7 days validity
- Header format: `Authorization: Bearer <token>`

## Roles

| Role | Description |
|------|-------------|
| USER | Regular authenticated user |
| ADMIN | Administrator with full access |

## Resources

### Auth (`/auth`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /auth/register | Register new user | No |
| POST | /auth/login | Login | No |
| POST | /auth/refresh | Refresh token | No |
| POST | /auth/logout | Logout | No |

### Users (`/users`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /users/me | Get current user profile | USER |
| PUT | /users/me | Update current user profile | USER |

### Admin Users (`/admin/users`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /admin/users | Get all users (paginated) | ADMIN |
| GET | /admin/users/{id} | Get user by ID | ADMIN |
| PATCH | /admin/users/{id}/deactivate | Deactivate user | ADMIN |
| PATCH | /admin/users/{id}/activate | Activate user | ADMIN |

### Categories (`/categories`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /categories | Get all categories (paginated) | No |
| GET | /categories/all | Get all categories (no pagination) | No |
| GET | /categories/{id} | Get category by ID | No |
| POST | /categories | Create category | ADMIN |
| PUT | /categories/{id} | Update category | ADMIN |
| DELETE | /categories/{id} | Delete category | ADMIN |

### Books (`/books`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /books | Get all books with filters | No |
| GET | /books/top | Get top 10 books by views | No |
| GET | /books/{id} | Get book by ID | No |
| POST | /books | Create book | ADMIN |
| PUT | /books/{id} | Update book | ADMIN |
| DELETE | /books/{id} | Delete book | ADMIN |

### Reviews (`/reviews`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /reviews/book/{bookId} | Get reviews for a book | No |
| GET | /reviews/user/{userId} | Get reviews by user | No |
| GET | /reviews/me | Get my reviews | USER |
| GET | /reviews/{id} | Get review by ID | No |
| POST | /reviews | Create review | USER |
| PUT | /reviews/{id} | Update review | USER |
| DELETE | /reviews/{id} | Delete review | USER |
| POST | /reviews/{id}/like | Like a review | USER |
| DELETE | /reviews/{id}/like | Unlike a review | USER |

### Cart (`/cart`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /cart | Get current user's cart | USER |
| POST | /cart/items | Add item to cart | USER |
| PUT | /cart/items/{itemId} | Update cart item quantity | USER |
| DELETE | /cart/items/{itemId} | Remove item from cart | USER |
| DELETE | /cart | Clear cart | USER |

### Orders (`/orders`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /orders | Get my orders | USER |
| GET | /orders/{id} | Get order by ID | USER |
| POST | /orders | Create order from cart | USER |
| PATCH | /orders/{id}/cancel | Cancel order | USER |

### Admin Orders (`/admin/orders`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /admin/orders | Get all orders | ADMIN |
| GET | /admin/orders/{id} | Get order by ID | ADMIN |
| PATCH | /admin/orders/{id}/status | Update order status | ADMIN |

### Favorites (`/favorites`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /favorites | Get my favorites | USER |
| POST | /favorites/{bookId} | Add to favorites | USER |
| DELETE | /favorites/{bookId} | Remove from favorites | USER |
| GET | /favorites/{bookId}/check | Check if favorited | USER |

### Admin Statistics (`/admin/stats`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /admin/stats/summary | Get summary statistics | ADMIN |

### Health (`/health`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /health | Health check | No |

## Query Parameters

### Pagination
| Parameter | Default | Description |
|-----------|---------|-------------|
| page | 0 | Page number (0-based) |
| size | 20 | Page size |
| sort | createdAt,desc | Sort field and direction |

### Book Filters
| Parameter | Description |
|-----------|-------------|
| keyword | Search in title and author |
| categoryId | Filter by category |
| minPrice | Minimum price |
| maxPrice | Maximum price |

## Error Codes

| HTTP Code | Error Code | Description |
|-----------|------------|-------------|
| 400 | BAD_REQUEST | Invalid request format |
| 400 | VALIDATION_FAILED | Field validation failed |
| 401 | UNAUTHORIZED | Invalid or missing token |
| 403 | FORBIDDEN | Access denied |
| 404 | RESOURCE_NOT_FOUND | Resource not found |
| 409 | DUPLICATE_RESOURCE | Resource already exists |
| 409 | STATE_CONFLICT | Invalid state transition |
| 429 | RATE_LIMITED | Too many requests |
| 500 | INTERNAL_SERVER_ERROR | Server error |