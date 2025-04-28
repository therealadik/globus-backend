package com.example.globus.repository;

import com.example.globus.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    
    @Query("SELECT l FROM LogEntry l WHERE l.timestamp BETWEEN :startDate AND :endDate ORDER BY l.timestamp DESC")
    List<LogEntry> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<LogEntry> findByLevelOrderByTimestampDesc(String level);
} 