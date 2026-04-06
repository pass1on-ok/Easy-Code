# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**EasyCode** is a Django-based educational platform with course management, video lessons, quizzes, Stripe payments, JWT authentication, and PDF certificate generation.

## Commands

All commands are run from the `project/` directory.

```bash
# Run development server
python manage.py runserver

# Apply migrations
python manage.py migrate

# Run all tests
pytest

# Run tests for a specific app
pytest courses/tests/
pytest exam/tests/

# Run a single test file
pytest courses/tests/test_views.py

# Install dependencies
pip install -r requirements.txt
```

## Architecture

### Django Apps

| App | Responsibility |
|-----|---------------|
| `courses/` | Course catalog, auth (signup/login), homepage |
| `exam/` | Quiz questions, answer submission, test results |
| `user_profile/` | Student/teacher profiles, certificate PDF generation |
| `user_payment/` | Stripe checkout, payment status, course reviews |
| `teacher/` | Teacher dashboard, course/stats management |

### URL Structure

- `/` — courses app (home, signup, login, course pages)
- `/user/` — profile, JWT token endpoints, certificate download
- `/exam/` — test-taking views and API
- `/teacher/` — teacher dashboard and course management
- `/payments/` — Stripe checkout and webhook
- `/swagger/`, `/redoc/` — auto-generated API docs (drf-yasg)

### Key Patterns

**Hybrid view architecture**: Template-based views for HTML pages + DRF API endpoints for JSON (used by Swagger and potential mobile clients).

**Authentication**: Django session auth for template views; SimpleJWT (`/user/token/`, `/user/token/refresh/`) for API clients.

**Role-based access**: `Profile.role` field (student/teacher) controls access. Teachers also have a separate `Teacher` model (one-to-one with User).

**Payment flow**: Course has a `stripe_product_id` → checkout creates a Stripe session → redirect → webhook sets `UserPayment.payment_bool = True` → user gets course access. Stripe keys are in `settings.py` (test keys).

**Course access control**: `Video.is_preview` controls whether non-enrolled users can watch. Enrollment is checked via `UserCourse` records.

**Certificate generation**: `user_profile/views.py::get_certificate` uses PyMuPDF (`fitz`) to replace placeholder text in `/files/resource/cert_template.pdf` and serves the output PDF.

### Core Model Relationships

```
User
  ├── Profile (1:1) — role: student | teacher
  ├── Teacher (1:1, teachers only)
  ├── UserCourse — enrollment + completion tracking
  ├── UserPayment — purchase records (payment_bool)
  ├── TestResult — exam scores
  └── UserAnswer — per-question responses

Course
  ├── Video (ordered by serial_number) — YouTube video_id
  │   └── Question — multiple choice, belongs to Video
  ├── Tag, Prerequisite, Learning — subclass CourseProperty
  ├── CourseMaterial — downloadable files
  └── Review — student ratings/comments
```

### Settings Notes

- Database: SQLite (`db.sqlite3`) — `project/settings.py`
- `CsrfViewMiddleware` is disabled — API-first design
- Media files served from `BASE_DIR/media/`
- `REDIRECT_DOMAIN = http://127.0.0.1:8000` (Stripe redirect base)