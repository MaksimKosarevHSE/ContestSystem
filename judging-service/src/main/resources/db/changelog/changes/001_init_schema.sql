CREATE TABLE IF NOT EXISTS processed_events (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(255) NOT NULL,
    CONSTRAINT uk_processed_events_message_id UNIQUE (message_id)
    );