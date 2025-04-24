package com.example.globus.dto.dashboard;

public record TransactionCountByPeriodDto(
        long weeklyCount,
        long monthlyCount,
        long quarterlyCount,
        long yearlyCount) {
}
