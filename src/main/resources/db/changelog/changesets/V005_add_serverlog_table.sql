--liquibase formatted sql

--changeset author:V005_add_serverlog_table
CREATE TABLE IF NOT EXISTS public.serverlog (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    level VARCHAR(10) NOT NULL,
    message TEXT NOT NULL,
    exception TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.serverlog IS 'Table for storing server logs';
COMMENT ON COLUMN public.serverlog.id IS 'Primary key';
COMMENT ON COLUMN public.serverlog.timestamp IS 'Log timestamp';
COMMENT ON COLUMN public.serverlog.level IS 'Log level (INFO, WARN, ERROR, etc.)';
COMMENT ON COLUMN public.serverlog.message IS 'Log message';
COMMENT ON COLUMN public.serverlog.exception IS 'Exception details if any';
COMMENT ON COLUMN public.serverlog.created_at IS 'Record creation timestamp'; 