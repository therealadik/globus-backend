package com.example.globus.service;

import com.example.globus.dto.dashboard.BankTransactionCountDto;
import com.example.globus.dto.dashboard.TransactionCountDto;
import com.example.globus.dto.dashboard.DebitCreditTransactionsDto;
import com.example.globus.dto.dashboard.IncomeExpenseComparisonDto;
import com.example.globus.dto.transaction.TransactionDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.mapstruct.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

/**
 * Рассчитывает статистику для дашборда.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionMapper transactionMapper;

    /**
     * Подсчитывает количество проведённых и отменённых транзакций.
     * @param transactions список транзакций
     * @return DTO с двумя показателями
     */

    public TransactionCountDto calculateTransactionCounts(List<Transaction> transactions) {
        long canceled = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.CANCELED
                        || t.getStatus() == TransactionStatus.DELETED)
                .count();
        long completed = transactions.size() - canceled;
        return new TransactionCountDto((int) completed, (int) canceled);
    }

    public DebitCreditTransactionsDto calculateDebitCreditTransactions(List<Transaction> transactions) {
        var creditTransactions = transactions.stream()
                .filter(t -> t.getTransactionType().equals(TransactionType.INCOME))
                .map(transactionMapper::toDto)
                .toList();

        var debitTransactions = transactions.stream()
                .filter(t -> t.getTransactionType().equals(TransactionType.EXPENSE))
                .map(transactionMapper::toDto)
                .toList();

        return new DebitCreditTransactionsDto(debitTransactions, creditTransactions);
    }

    public IncomeExpenseComparisonDto  calculateIncomeExpenseComparison(List<Transaction> transactions) {
        var incomeAmount = transactions.stream()
                .filter(t -> t.getTransactionType().equals(TransactionType.INCOME))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO,  BigDecimal::add);

        var  expenseAmount = transactions.stream()
                .filter(t -> t.getTransactionType().equals(TransactionType.EXPENSE))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO,  BigDecimal::add);

        return new IncomeExpenseComparisonDto(incomeAmount, expenseAmount);
    }

    public List<BankTransactionCountDto> calculateBankStatistics(List<TransactionDto> transactions) {
        Map<String, Long> groupedTransactions = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getBankSender() + "|" + t.getBankReceiver(),
                        Collectors.counting()
                ));

        return groupedTransactions.entrySet().stream()
                .map(entry -> {
                    String[] banks = entry.getKey().split("\\|" );
                    String senderBankName = banks[0];
                    String receiverBankName = banks[1];
                    long transactionCount = entry.getValue();
                    return new BankTransactionCountDto(senderBankName, receiverBankName, transactionCount);
                })
                .sorted(Comparator.comparingLong(BankTransactionCountDto::getTransactionCount).reversed())
                .collect(Collectors.toList());
    }
}