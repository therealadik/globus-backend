package com.example.globus.dto.dashboard;

public record MonthlyTransactionCountDTO(
    int year,
    int month, // 1-12
    long transactionCount
) {
}
