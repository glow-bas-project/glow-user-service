--liquibase formatted sql
--changeset edins:01_04_2026-1
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    keycloak_id VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS user_permissions (
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer_users (
    id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS customer_addresses (
    user_id VARCHAR(36) NOT NULL REFERENCES customer_users(id) ON DELETE CASCADE,
    address VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS courier_users (
    id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    vehicle_type VARCHAR(50),
    courier_availability VARCHAR(50),
    stripe_account_id VARCHAR(255),
    stripe_onboarding_complete BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS restaurant_users (
    id VARCHAR(36) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    restaurant_id VARCHAR(36)
);