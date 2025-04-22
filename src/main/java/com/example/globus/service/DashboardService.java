package com.example.globus.service;

import com.example.globus.dto.dashboard.DashboardTransactionCountDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Рассчитывает статистику для дашборда.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    /**
     * Подсчитывает количество проведённых и отменённых транзакций.
     * @param transactions список транзакций
     * @return DTO с двумя показателями
     */
    public DashboardTransactionCountDto calculateTransactionCounts(List<Transaction> transactions) {
        long canceled = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.CANCELED
                        || t.getStatus() == TransactionStatus.DELETED)
                .count();
        long completed = transactions.size() - canceled;
        return new DashboardTransactionCountDto((int) completed, (int) canceled);
    }
}