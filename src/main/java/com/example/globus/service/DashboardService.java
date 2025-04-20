package com.example.globus.service;

import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int DECIMAL_PLACES = 2;

    public DashboardDebitResponse getDebitTransactionsDashboard(List<Transaction> transactions) {
        if (transactions == null) {
            return createEmptyDebitDashboard();
        }

        List<Transaction> debitTransactions = transactions.stream()
                .filter(Objects::nonNull)
                .filter(transaction -> transaction.getAmount() != null)
                .filter(transaction -> transaction.getStatus() == TransactionStatus.COMPLETED)
                .filter(transaction -> transaction.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.toList());

        BigDecimal totalDebitAmount = debitTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs()
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);

        Map<String, BigDecimal> debitsByCategory = debitTransactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().abs().setScale(DECIMAL_PLACES, RoundingMode.HALF_UP)
                ));

        Map<String, Long> transactionCountByCategory = debitTransactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.counting()));

        BigDecimal averageDebitAmount = calculateAverage(totalDebitAmount, debitTransactions.size());

        return DashboardDebitResponse.builder()
                .totalDebitAmount(totalDebitAmount)
                .totalDebitTransactions(debitTransactions.size())
                .debitsByCategory(debitsByCategory)
                .transactionCountByCategory(transactionCountByCategory)
                .averageDebitAmount(averageDebitAmount)
                .build();
    }

    public DashboardCreditResponse getCreditTransactionsDashboard(List<Transaction> transactions) {
        if (transactions == null) {
            return createEmptyCreditDashboard();
        }

        List<Transaction> creditTransactions = transactions.stream()
                .filter(Objects::nonNull)
                .filter(transaction -> transaction.getAmount() != null)
                .filter(transaction -> transaction.getStatus() == TransactionStatus.COMPLETED)
                .filter(transaction -> transaction.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        BigDecimal totalCreditAmount = creditTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);

        Map<String, BigDecimal> creditsByCategory = creditTransactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().setScale(DECIMAL_PLACES, RoundingMode.HALF_UP)
                ));

        Map<String, Long> transactionCountByCategory = creditTransactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.counting()));

        BigDecimal averageCreditAmount = calculateAverage(totalCreditAmount, creditTransactions.size());

        return DashboardCreditResponse.builder()
                .totalCreditAmount(totalCreditAmount)
                .totalCreditTransactions(creditTransactions.size())
                .creditsByCategory(creditsByCategory)
                .transactionCountByCategory(transactionCountByCategory)
                .averageCreditAmount(averageCreditAmount)
                .build();
    }

    private BigDecimal calculateAverage(BigDecimal total, int count) {
        if (count == 0) {
            return BigDecimal.ZERO.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
        }
        return total.divide(BigDecimal.valueOf(count), DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    private DashboardDebitResponse createEmptyDebitDashboard() {
        return DashboardDebitResponse.builder()
                .totalDebitAmount(BigDecimal.ZERO.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP))
                .totalDebitTransactions(0)
                .debitsByCategory(Collections.emptyMap())
                .transactionCountByCategory(Collections.emptyMap())
                .averageDebitAmount(BigDecimal.ZERO.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP))
                .build();
    }

    private DashboardCreditResponse createEmptyCreditDashboard() {
        return DashboardCreditResponse.builder()
                .totalCreditAmount(BigDecimal.ZERO.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP))
                .totalCreditTransactions(0)
                .creditsByCategory(Collections.emptyMap())
                .transactionCountByCategory(Collections.emptyMap())
                .averageCreditAmount(BigDecimal.ZERO.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP))
                .build();
    }

    public void generateDashboards(List<Transaction> transactions) {
        DashboardDebitResponse debitDashboard = getDebitTransactionsDashboard(transactions);
        DashboardCreditResponse creditDashboard = getCreditTransactionsDashboard(transactions);

        processDashboards(debitDashboard, creditDashboard);
    }

    private void processDashboards(DashboardDebitResponse debitDashboard, DashboardCreditResponse creditDashboard) {
        // Add processing logic here if needed
    }
}
