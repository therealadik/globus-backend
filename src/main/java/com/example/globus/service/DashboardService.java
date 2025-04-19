package com.example.globus.service;

import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    public DashboardDebitResponse getDebitTransactionsDashboard(List<Transaction> transactions) {
        List<Transaction> debitTransactions = transactions.stream()
                .filter(transaction -> transaction.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.toList());

        BigDecimal totalDebitAmount = debitTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs();

        Map<Category, BigDecimal> debitsByCategory = debitTransactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().abs()
                ));

        Map<Category, Long> transactionCountByCategory = debitTransactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.counting()));

        BigDecimal averageDebitAmount = debitTransactions.isEmpty() ? BigDecimal.ZERO :
                totalDebitAmount.divide(BigDecimal.valueOf(debitTransactions.size()), 2, RoundingMode.HALF_UP);

        return DashboardDebitResponse.builder()
                .totalDebitAmount(totalDebitAmount)
                .totalDebitTransactions(debitTransactions.size())
                .debitsByCategory(debitsByCategory)
                .transactionCountByCategory(transactionCountByCategory)
                .averageDebitAmount(averageDebitAmount)
                .build();
    }

    public DashboardCreditResponse getCreditTransactionsDashboard(List<Transaction> transactions) {
        List<Transaction> creditTransactions = transactions.stream()
                .filter(transaction -> transaction.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        BigDecimal totalCreditAmount = creditTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Category, BigDecimal> creditsByCategory = creditTransactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)));

        Map<Category, Long> transactionCountByCategory = creditTransactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.counting()));

        BigDecimal averageCreditAmount = creditTransactions.isEmpty() ? BigDecimal.ZERO :
                totalCreditAmount.divide(BigDecimal.valueOf(creditTransactions.size()), 2, RoundingMode.HALF_UP);

        return DashboardCreditResponse.builder()
                .totalCreditAmount(totalCreditAmount)
                .totalCreditTransactions(creditTransactions.size())
                .creditsByCategory(creditsByCategory)
                .transactionCountByCategory(transactionCountByCategory)
                .averageCreditAmount(averageCreditAmount)
                .build();
    }

    public void generateDashboards(List<Transaction> transactions) {
        DashboardDebitResponse debitDashboard = getDebitTransactionsDashboard(transactions);
        DashboardCreditResponse creditDashboard = getCreditTransactionsDashboard(transactions);

        processDashboards(debitDashboard, creditDashboard);
    }

    private void processDashboards(DashboardDebitResponse debitDashboard, DashboardCreditResponse creditDashboard) {

    }
}
