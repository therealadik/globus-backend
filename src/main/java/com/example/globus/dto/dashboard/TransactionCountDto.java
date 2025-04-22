package com.example.globus.dto.dashboard;

/**
 * Результат подсчёта транзакций для дашборда.
 *
 * @param completedCount проведённых транзакций
 * @param canceledCount  отменённых транзакций
 */
public record TransactionCountDto(
        int completedCount,
        int canceledCount) {
}