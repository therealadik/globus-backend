package com.example.globus.service;

import com.example.globus.entity.LogEntry;
import com.example.globus.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogEntryRepository logEntryRepository;

    public List<LogEntry> getAllLogs() {
        return logEntryRepository.findAll();
    }

    public List<LogEntry> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return logEntryRepository.findByDateRange(startDate, endDate);
    }

    public List<LogEntry> getLogsByLevel(String level) {
        return logEntryRepository.findByLevelOrderByTimestampDesc(level);
    }

    public void saveLog(LogEntry logEntry) {
        logEntryRepository.save(logEntry);
    }
} 