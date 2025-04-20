package com.example.globus.service;

import com.example.globus.dto.dashboard.DashboardTransactionCountDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    /**
     * Подсчитывает количество проведенных и отмененных транзакций
     * @param transactions список транзакций
     * @return DTO с количеством проведенных и отмененных транзакций
     */
    public DashboardTransactionCountDto calculateTransactionCounts(List<Transaction> transactions) {
        int canceledCount = (int) transactions.stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.CANCELED
                        || transaction.getStatus() == TransactionStatus.DELETED)
                .count();

        int completedCount = transactions.size() - canceledCount;

        return new DashboardTransactionCountDto(completedCount, canceledCount);
    }
}
