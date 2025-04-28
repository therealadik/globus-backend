package com.example.globus.controller;

import com.example.globus.entity.LogEntry;
import com.example.globus.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @GetMapping
    public ResponseEntity<List<LogEntry>> getAllLogs() {
        return ResponseEntity.ok(logService.getAllLogs());
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LogEntry>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(logService.getLogsByDateRange(startDate, endDate));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<LogEntry>> getLogsByLevel(@PathVariable String level) {
        return ResponseEntity.ok(logService.getLogsByLevel(level));
    }
} 