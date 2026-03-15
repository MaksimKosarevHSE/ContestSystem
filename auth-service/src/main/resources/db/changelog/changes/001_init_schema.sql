CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    handle VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(511) NOT NULL,

    CONSTRAINT uk_users_handle UNIQUE (handle),
    CONSTRAINT uk_users_email UNIQUE (email)
    );
