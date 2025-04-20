package com.example.globus.service;

import com.example.globus.dto.dashboard.DashboardTransactionCountDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DashboardServiceTest {

    private final DashboardService dashboardService = new DashboardService();

    @Test
    void calculateTransactionCounts() {
        List<Transaction> transactions = List.of(
                Transaction.builder().status(TransactionStatus.NEW).build(),
                Transaction.builder().status(TransactionStatus.COMPLETED).build(),
                Transaction.builder().status(TransactionStatus.DELETED).build(),
                Transaction.builder().status(TransactionStatus.CANCELED).build(),
                Transaction.builder().status(TransactionStatus.PROCESSING).build()
        );

        DashboardTransactionCountDto result = dashboardService.calculateTransactionCounts(transactions);

        assertEquals(3, result.completedCount());
        assertEquals(2, result.canceledCount());
    }
}
