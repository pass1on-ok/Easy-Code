-- =============================================================
-- EasyCode Platform - Database Schema
-- MySQL 8.0+
-- Run: mysql -u root -p easycode < schema.sql
-- =============================================================

CREATE DATABASE IF NOT EXISTS easycode CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE easycode;

-- -------------------------------------------------------------
-- Users table (maps to Spring Security UserDetails)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(150) NOT NULL UNIQUE,
    email       VARCHAR(254) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,          -- BCrypt encoded
    first_name  VARCHAR(150),
    last_name   VARCHAR(150),
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Profiles (role: STUDENT | TEACHER)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS profiles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL UNIQUE,
    role        VARCHAR(20) NOT NULL DEFAULT 'STUDENT',  -- STUDENT | TEACHER
    avatar      VARCHAR(500),
    bio         TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_profiles_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Refresh token blacklist (for logout / token rotation)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expires_at  DATETIME NOT NULL,
    revoked     TINYINT(1) NOT NULL DEFAULT 0,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_rt_token (token),
    INDEX idx_rt_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Courses
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS courses (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price       INT NOT NULL DEFAULT 0,         -- price in tenge (₸), 0 = free
    discount    INT NOT NULL DEFAULT 0,         -- discount percentage 0-100
    thumbnail   VARCHAR(500),
    is_published TINYINT(1) NOT NULL DEFAULT 1,
    stripe_product_id  VARCHAR(255),
    stripe_price_id    VARCHAR(255),
    teacher_id  BIGINT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_courses_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_courses_slug (slug),
    INDEX idx_courses_teacher (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Course tags (tag name stored directly for simplicity)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS course_tags (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id   BIGINT NOT NULL,
    tag         VARCHAR(100) NOT NULL,
    CONSTRAINT fk_tags_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_tags_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Course prerequisites (text description)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS course_prerequisites (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id   BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    CONSTRAINT fk_prereq_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_prereq_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- What you will learn (learning outcomes)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS course_learnings (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id   BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    CONSTRAINT fk_learning_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_learning_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Videos / Lessons
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS videos (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id     BIGINT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    description   TEXT,
    video_url     VARCHAR(500),                -- YouTube embed URL or direct URL
    serial_number INT NOT NULL DEFAULT 1,      -- ordering within course
    is_preview    TINYINT(1) NOT NULL DEFAULT 0,
    duration_sec  INT,                         -- video duration in seconds
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_videos_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_videos_course (course_id),
    UNIQUE KEY uq_video_order (course_id, serial_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Course materials (downloadable files)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS course_materials (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id   BIGINT NOT NULL,
    title       VARCHAR(255) NOT NULL,
    file_url    VARCHAR(500) NOT NULL,
    description TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_materials_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_materials_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- User <-> Course enrollment
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_courses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    course_id       BIGINT NOT NULL,
    enrollment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed       TINYINT(1) NOT NULL DEFAULT 0,
    grade           INT,                        -- final exam grade (0-100)
    CONSTRAINT fk_uc_user   FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_uc_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_course (user_id, course_id),
    INDEX idx_uc_user (user_id),
    INDEX idx_uc_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Payments
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_payments (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    course_id           BIGINT NOT NULL,
    stripe_session_id   VARCHAR(255),
    stripe_payment_intent VARCHAR(255),
    amount              INT NOT NULL DEFAULT 0, -- amount paid in tenge
    payment_bool        TINYINT(1) NOT NULL DEFAULT 0,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pay_user   FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_pay_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_pay_user (user_id),
    INDEX idx_pay_session (stripe_session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Reviews
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    course_id   BIGINT NOT NULL,
    rating      INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user   FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_review_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_review (user_id, course_id),
    INDEX idx_review_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------
-- Video progress tracking
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS video_progress (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    video_id        BIGINT NOT NULL,
    watched_seconds INT NOT NULL DEFAULT 0,
    completed       TINYINT(1) NOT NULL DEFAULT 0,
    last_watched_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_vp_user  FOREIGN KEY (user_id)  REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_vp_video FOREIGN KEY (video_id) REFERENCES videos(id)  ON DELETE CASCADE,
    UNIQUE KEY uq_user_video (user_id, video_id),
    INDEX idx_vp_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
