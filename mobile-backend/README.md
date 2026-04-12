# EasyCode Backend — Java Spring Boot

RESTful API backend for the EasyCode online programming education platform.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.3.4 |
| Language | Java 17 |
| Database | MySQL 8.0+ |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security 6 + JWT (jjwt 0.12) |
| Payments | Stripe Java SDK 26.x |
| Build | Maven |

---

## Project Structure

```
src/main/java/com/easycode/backend/
├── EasyCodeApplication.java        # Entry point
├── config/
│   ├── SecurityConfig.java         # Spring Security + JWT filter chain
│   ├── CorsConfig.java             # CORS allowed origins
│   └── JacksonConfig.java          # snake_case JSON naming
├── controller/
│   ├── AuthController.java         # /api/token/, /api/signup/, /api/token/refresh/
│   ├── UserController.java         # /user/api/me/
│   ├── CourseController.java       # /api/courses/, /api/course/{slug}/, etc.
│   └── PaymentController.java      # /api/create-checkout/, /api/confirm-payment/
├── service/
│   ├── AuthService.java + impl/
│   ├── UserService.java + impl/
│   ├── CourseService.java + impl/
│   └── PaymentService.java + impl/
├── repository/                     # Spring Data JPA repositories
├── entity/                         # JPA entities
├── dto/
│   ├── request/                    # Incoming request bodies
│   └── response/                   # Outgoing response VOs
├── security/
│   ├── JwtService.java             # Token generation & validation
│   ├── JwtAuthFilter.java          # Bearer token extraction filter
│   └── UserDetailsServiceImpl.java
├── exception/
│   ├── GlobalExceptionHandler.java # @RestControllerAdvice
│   └── *.java                      # Typed exception classes
└── common/
    └── Result.java                 # Unified { code, message, data } wrapper
```

---

## API Summary

| # | Method | Endpoint | Auth | Description |
|---|--------|----------|------|-------------|
| 1 | POST | `/api/token/` | No | Login → returns `{access, refresh, user}` |
| 2 | POST | `/api/signup/` | No | Register → returns `{access, refresh, user}` |
| 3 | POST | `/api/token/refresh/` | No | Refresh access token (with rotation) |
| 4 | GET | `/user/api/me/` | Yes | Get current user profile |
| 5 | PATCH | `/user/api/me/` | Yes | Update profile (first_name, last_name, email, bio, avatar) |
| 6 | GET | `/api/courses/` | No | List all published courses |
| 7 | GET | `/api/course/{slug}/` | Optional | Course detail (videos locked if not enrolled) |
| 8 | GET | `/api/purchased-courses/` | Yes | List enrolled courses with progress |
| 9 | POST | `/check-out/{slug}/` | Yes | Enroll in a free course |
| 10 | POST | `/api/create-checkout/{slug}/` | Yes | Create Stripe checkout session |
| 11 | POST | `/api/confirm-payment/` | Yes | Verify Stripe payment & enroll user |
| 12 | GET | `/api/stripe-key/` | No | Get Stripe publishable key |

---

## Database Schema

See `src/main/resources/schema.sql` for full DDL. Key tables:

```
users            — accounts (username, email, bcrypt password)
profiles         — role extension (STUDENT / TEACHER)
refresh_tokens   — stored JWT refresh tokens for rotation/revocation
courses          — course catalog with slug, price, discount, Stripe IDs
videos           — lessons ordered by serial_number per course
course_materials — downloadable files per course
user_courses     — enrollment + completion + grade
user_payments    — Stripe payment records
reviews          — 1-5 star ratings per user/course
video_progress   — per-video watch tracking
```

---

## Setup & Run

### 1. Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8.0+

### 2. Database

```sql
CREATE DATABASE easycode CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Or run the full schema:

```bash
mysql -u root -p easycode < src/main/resources/schema.sql
```

### 3. Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring.datasource.password: your_mysql_password
stripe.public-key: pk_test_...
stripe.secret-key: sk_test_...
jwt.secret: <64-char hex string>        # generate with: openssl rand -hex 32
app.frontend-url: http://localhost:5173
```

### 4. Build & Run

```bash
# From the mobile-backend directory:
mvn clean package -DskipTests
java -jar target/easy-code-backend-1.0.0.jar

# Or run directly with Maven:
mvn spring-boot:run
```

The server starts on **http://localhost:8080**.

### 5. Quick API test

```bash
# Register
curl -X POST http://localhost:8080/api/signup/ \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"pass1234","password2":"pass1234"}'

# Login
curl -X POST http://localhost:8080/api/token/ \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"pass1234"}'

# Get courses (public)
curl http://localhost:8080/api/courses/

# Get profile (authenticated)
curl http://localhost:8080/user/api/me/ \
  -H "Authorization: Bearer <access_token>"
```

---

## Connecting the Frontend

In the frontend's `src/services/api.ts`, change the base URL to:

```typescript
const BASE_URL = 'http://localhost:8080'
```

All 12 API endpoints are path-compatible with the existing frontend code.

---

## JWT Token Rotation

- Access tokens expire in **24 hours** (configurable via `jwt.expiration`)
- Refresh tokens expire in **7 days** (configurable via `jwt.refresh-expiration`)
- Each `/api/token/refresh/` call issues a **new refresh token** and revokes the old one
- Tokens are stored in `refresh_tokens` table for revocation support
