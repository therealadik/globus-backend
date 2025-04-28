--liquibase formatted sql

--changeset author:V007_add_test_serverlogs
INSERT INTO public.serverlog (timestamp, level, message, exception, created_at) VALUES
    (CURRENT_TIMESTAMP - INTERVAL '1 hour', 'INFO', 'Application started successfully', NULL, CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '55 minutes', 'WARN', 'Slow database query detected', 'Query execution time exceeded threshold', CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '50 minutes', 'ERROR', 'Failed to connect to external service', 'Connection timeout after 5000ms', CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '45 minutes', 'INFO', 'User authentication successful', NULL, CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '40 minutes', 'ERROR', 'Database connection lost', 'Network error: Connection refused', CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '35 minutes', 'INFO', 'Cache cleared successfully', NULL, CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'WARN', 'High memory usage detected', 'Memory usage at 85%', CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '25 minutes', 'INFO', 'Scheduled task completed', NULL, CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '20 minutes', 'ERROR', 'File not found', 'File path: /var/log/app.log', CURRENT_TIMESTAMP),
    (CURRENT_TIMESTAMP - INTERVAL '15 minutes', 'INFO', 'Configuration reloaded', NULL, CURRENT_TIMESTAMP); 