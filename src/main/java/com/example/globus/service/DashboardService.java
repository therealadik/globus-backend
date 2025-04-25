package com.example.globus.service;

import com.example.globus.dto.dashboard.*;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.mapstruct.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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

    public List<BankTransactionStatisticsDto> calculateBankStatistics(List<Transaction> transactions) {
        Map<String, Long> groupedTransactions = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getBankSender().getName() + "|" + t.getBankReceiver().getName(),
                        Collectors.counting()
                ));

        return groupedTransactions.entrySet().stream()
                .map(entry -> {
                    String[] banks = entry.getKey().split("\\|" );
                    String senderBankName = banks[0];
                    String receiverBankName = banks[1];
                    long transactionCount = entry.getValue();
                    return new BankTransactionStatisticsDto(senderBankName, receiverBankName, transactionCount);
                })
                .sorted(Comparator.comparingLong(BankTransactionStatisticsDto::transactionCount).reversed())
                .collect(Collectors.toList());
    }

    public TransactionCategoryStatsDto calculateTransactionCategoryStats(List<Transaction> transactions) {
        Map<String, BigDecimal> incomeByCategory = transactions.stream()
                .filter(t -> t.getTransactionType().equals(TransactionType.INCOME))
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        Map<String, BigDecimal> expenseByCategory = transactions.stream()
                .filter(t -> t.getTransactionType().equals(TransactionType.EXPENSE))
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, t -> t.getAmount().abs(), BigDecimal::add)
                ));

        return new TransactionCategoryStatsDto(incomeByCategory, expenseByCategory);
    }
    public TransactionCountByPeriodDto caclulateStatsPerPeriods(List<Transaction> transactions, LocalDateTime now) {

        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
        LocalDateTime startOfQuarter = LocalDateTime.of(
                now.getYear(),
                (currentQuarter - 1) * 3 + 1, // Первый месяц квартала (1, 4, 7, 10)
                1, 0, 0
        );

        LocalDateTime startOfYear = LocalDateTime.of(now.getYear(), 1, 1, 0, 0);

        long weeklyCount = 0L;
        long monthlyCount = 0L;
        long quarterlyCount = 0L;
        long yearlyCount = 0L;

        for (Transaction t : transactions) {
            if (t.getTransactionDate().isAfter(startOfWeek) || t.getTransactionDate().isEqual(startOfWeek)) {
                weeklyCount++;
            }
            if (t.getTransactionDate().isAfter(startOfMonth) || t.getTransactionDate().isEqual(startOfMonth)) {
                monthlyCount++;
            }
            if (t.getTransactionDate().isAfter(startOfQuarter) || t.getTransactionDate().isEqual(startOfQuarter)) {
                quarterlyCount++;
            }
            if (t.getTransactionDate().isAfter(startOfYear) || t.getTransactionDate().isEqual(startOfYear)) {
                yearlyCount++;
            }
        }
        return new TransactionCountByPeriodDto(weeklyCount, monthlyCount, quarterlyCount, yearlyCount);
    }
}