# Database Schema - Bookstore

## Entity Relationship Diagram
```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   users     │       │   books     │       │ categories  │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ user_id PK  │       │ book_id PK  │       │category_id PK│
│ email       │       │ title       │       │ name        │
│ password    │       │ author      │       │ description │
│ username    │       │ publisher   │       │ created_at  │
│ phone       │       │ summary     │       │ updated_at  │
│ role        │       │ isbn        │       └─────────────┘
│ birth_date  │       │ price       │              │
│ bio         │       │ pub_date    │              │
│ is_active   │       │ stock_qty   │              │
│ created_at  │       │ view_count  │              │
│ updated_at  │       │ is_active   │              │
└─────────────┘       │ created_at  │              │
      │               │ updated_at  │              │
      │               └─────────────┘              │
      │                     │                      │
      │                     │    ┌─────────────────┘
      │                     │    │
      │               ┌─────┴────┴───┐
      │               │book_categories│
      │               ├──────────────┤
      │               │ book_id FK   │
      │               │ category_id FK│
      │               └──────────────┘
      │
      ├──────────────────┬──────────────────┐
      │                  │                  │
      ▼                  ▼                  ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│  reviews    │   │   carts     │   │  favorites  │
├─────────────┤   ├─────────────┤   ├─────────────┤
│ review_id PK│   │ cart_id PK  │   │favorite_id PK│
│ user_id FK  │   │ user_id FK  │   │ user_id FK  │
│ book_id FK  │   │ created_at  │   │ book_id FK  │
│ rating      │   │ updated_at  │   │ created_at  │
│ comment     │   └─────────────┘   └─────────────┘
│ created_at  │         │
│ updated_at  │         ▼
│ deleted_at  │   ┌─────────────┐
└─────────────┘   │ cart_items  │
      │           ├─────────────┤
      ▼           │cart_item_id PK│
┌─────────────┐   │ cart_id FK  │
│review_likes │   │ book_id FK  │
├─────────────┤   │ quantity    │
│ like_id PK  │   │ created_at  │
│ review_id FK│   │ updated_at  │
│ user_id FK  │   └─────────────┘
│ created_at  │
└─────────────┘

┌─────────────┐   ┌─────────────┐
│   orders    │   │ order_items │
├─────────────┤   ├─────────────┤
│ order_id PK │──▶│order_item_id PK│
│ user_id FK  │   │ order_id FK │
│ total_amount│   │ book_id FK  │
│ status      │   │ quantity    │
│ created_at  │   │ unit_price  │
│ updated_at  │   │ total_price │
└─────────────┘   │ created_at  │
                  └─────────────┘
```

## Tables

### users
| Column | Type | Constraints |
|--------|------|-------------|
| user_id | BIGINT | PK, AUTO_INCREMENT |
| email | VARCHAR(100) | NOT NULL, UNIQUE |
| password_hash | VARCHAR(255) | NOT NULL |
| username | VARCHAR(50) | NOT NULL |
| phone_number | VARCHAR(20) | |
| role | ENUM('USER','ADMIN') | NOT NULL, DEFAULT 'USER' |
| birth_date | DATE | |
| bio | TEXT | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | |

### books
| Column | Type | Constraints |
|--------|------|-------------|
| book_id | BIGINT | PK, AUTO_INCREMENT |
| title | VARCHAR(500) | NOT NULL |
| author | VARCHAR(200) | |
| publisher | VARCHAR(200) | |
| summary | TEXT | |
| isbn | VARCHAR(20) | UNIQUE |
| price | DECIMAL(10,2) | NOT NULL |
| publication_date | DATE | |
| stock_quantity | INT | NOT NULL, DEFAULT 0 |
| view_count | BIGINT | NOT NULL, DEFAULT 0 |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | |
| deleted_at | DATETIME | |

### categories
| Column | Type | Constraints |
|--------|------|-------------|
| category_id | BIGINT | PK, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL, UNIQUE |
| description | VARCHAR(500) | |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | |

### book_categories
| Column | Type | Constraints |
|--------|------|-------------|
| book_id | BIGINT | FK → books |
| category_id | BIGINT | FK → categories |
| | | PK(book_id, category_id) |

### reviews
| Column | Type | Constraints |
|--------|------|-------------|
| review_id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users, NOT NULL |
| book_id | BIGINT | FK → books, NOT NULL |
| rating | INT | NOT NULL (1-5) |
| comment | TEXT | |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | |
| deleted_at | DATETIME | |

### review_likes
| Column | Type | Constraints |
|--------|------|-------------|
| like_id | BIGINT | PK, AUTO_INCREMENT |
| review_id | BIGINT | FK → reviews, NOT NULL |
| user_id | BIGINT | FK → users, NOT NULL |
| created_at | DATETIME | NOT NULL |
| | | UNIQUE(review_id, user_id) |

### carts
| Column | Type | Constraints |
|--------|------|-------------|
| cart_id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users, NOT NULL, UNIQUE |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | |

### cart_items
| Column | Type | Constraints |
|--------|------|-------------|
| cart_item_id | BIGINT | PK, AUTO_INCREMENT |
| cart_id | BIGINT | FK → carts, NOT NULL |
| book_id | BIGINT | FK → books, NOT NULL |
| quantity | INT | NOT NULL, DEFAULT 1 |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | |
| | | UNIQUE(cart_id, book_id) |

### orders
| Column | Type | Constraints |
|--------|------|-------------|
| order_id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users, NOT NULL |
| total_amount | DECIMAL(12,2) | NOT NULL |
| status | ENUM | NOT NULL, DEFAULT 'CREATED' |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | |

Order Status: CREATED, PAID, SHIPPED, DELIVERED, CANCELLED

### order_items
| Column | Type | Constraints |
|--------|------|-------------|
| order_item_id | BIGINT | PK, AUTO_INCREMENT |
| order_id | BIGINT | FK → orders, NOT NULL |
| book_id | BIGINT | FK → books, NOT NULL |
| quantity | INT | NOT NULL |
| unit_price | DECIMAL(10,2) | NOT NULL |
| total_price | DECIMAL(12,2) | NOT NULL |
| created_at | DATETIME | NOT NULL |

### favorites
| Column | Type | Constraints |
|--------|------|-------------|
| favorite_id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users, NOT NULL |
| book_id | BIGINT | FK → books, NOT NULL |
| created_at | DATETIME | NOT NULL |
| | | UNIQUE(user_id, book_id) |

## Indexes

| Table | Index | Columns |
|-------|-------|---------|
| users | idx_users_email | email |
| books | idx_books_title | title |
| books | idx_books_author | author |
| books | idx_books_isbn | isbn |
| reviews | idx_reviews_book | book_id |
| reviews | idx_reviews_user | user_id |
| orders | idx_orders_user | user_id |
| orders | idx_orders_status | status |