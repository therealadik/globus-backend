CREATE TABLE log_entries (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    logger_name VARCHAR(255),
    thread_name VARCHAR(255)
);

-- Создаем индекс для быстрого поиска по дате
CREATE INDEX idx_log_entries_timestamp ON log_entries(timestamp);

-- Создаем индекс для быстрого поиска по уровню лога
CREATE INDEX idx_log_entries_level ON log_entries(level); 