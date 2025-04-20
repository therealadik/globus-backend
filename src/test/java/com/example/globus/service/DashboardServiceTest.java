package com.example.globus.service;

import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    private Category category1;
    private Category category2;
    private Transaction debitTransaction1;
    private Transaction debitTransaction2;
    private Transaction creditTransaction1;
    private Transaction creditTransaction2;
    private Transaction nullCategoryTransaction;
    private Transaction nullAmountTransaction;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setName("Food");

        category2 = new Category();
        category2.setName("Transport");

        debitTransaction1 = Transaction.builder()
                .amount(BigDecimal.valueOf(-100))
                .category(category1)
                .status(TransactionStatus.COMPLETED)
                .build();

        debitTransaction2 = Transaction.builder()
                .amount(BigDecimal.valueOf(-50))
                .category(category2)
                .status(TransactionStatus.COMPLETED)
                .build();

        creditTransaction1 = Transaction.builder()
                .amount(BigDecimal.valueOf(200))
                .category(category1)
                .status(TransactionStatus.COMPLETED)
                .build();

        creditTransaction2 = Transaction.builder()
                .amount(BigDecimal.valueOf(150))
                .category(category2)
                .status(TransactionStatus.COMPLETED)
                .build();

        nullCategoryTransaction = Transaction.builder()
                .amount(BigDecimal.valueOf(-75))
                .status(TransactionStatus.COMPLETED)
                .build();

        nullAmountTransaction = Transaction.builder()
                .category(category1)
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    @Test
    void getDebitTransactionsDashboard_WithValidTransactions_ShouldReturnCorrectDashboard() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(debitTransaction1, debitTransaction2, creditTransaction1);

        // Act
        DashboardDebitResponse response = dashboardService.getDebitTransactionsDashboard(transactions);

        // Assert
        assertEquals(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP), response.getTotalDebitAmount());
        assertEquals(2, response.getTotalDebitTransactions());
        assertEquals(BigDecimal.valueOf(75).setScale(2, RoundingMode.HALF_UP), response.getAverageDebitAmount());

        Map<String, BigDecimal> debitsByCategory = response.getDebitsByCategory();
        assertEquals(2, debitsByCategory.size());
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), debitsByCategory.get("Food"));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP), debitsByCategory.get("Transport"));

        Map<String, Long> countByCategory = response.getTransactionCountByCategory();
        assertEquals(2, countByCategory.size());
        assertEquals(1L, countByCategory.get("Food"));
        assertEquals(1L, countByCategory.get("Transport"));
    }

    @Test
    void getCreditTransactionsDashboard_WithValidTransactions_ShouldReturnCorrectDashboard() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(creditTransaction1, creditTransaction2, debitTransaction1);

        // Act
        DashboardCreditResponse response = dashboardService.getCreditTransactionsDashboard(transactions);

        // Assert
        assertEquals(BigDecimal.valueOf(350).setScale(2, RoundingMode.HALF_UP), response.getTotalCreditAmount());
        assertEquals(2, response.getTotalCreditTransactions());
        assertEquals(BigDecimal.valueOf(175).setScale(2, RoundingMode.HALF_UP), response.getAverageCreditAmount());

        Map<String, BigDecimal> creditsByCategory = response.getCreditsByCategory();
        assertEquals(2, creditsByCategory.size());
        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), creditsByCategory.get("Food"));
        assertEquals(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP), creditsByCategory.get("Transport"));

        Map<String, Long> countByCategory = response.getTransactionCountByCategory();
        assertEquals(2, countByCategory.size());
        assertEquals(1L, countByCategory.get("Food"));
        assertEquals(1L, countByCategory.get("Transport"));
    }

    @Test
    void getDebitTransactionsDashboard_WithNullTransactions_ShouldReturnEmptyDashboard() {
        // Act
        DashboardDebitResponse response = dashboardService.getDebitTransactionsDashboard(null);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getTotalDebitAmount());
        assertEquals(0, response.getTotalDebitTransactions());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getAverageDebitAmount());
        assertTrue(response.getDebitsByCategory().isEmpty());
        assertTrue(response.getTransactionCountByCategory().isEmpty());
    }

    @Test
    void getCreditTransactionsDashboard_WithNullTransactions_ShouldReturnEmptyDashboard() {
        // Act
        DashboardCreditResponse response = dashboardService.getCreditTransactionsDashboard(null);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getTotalCreditAmount());
        assertEquals(0, response.getTotalCreditTransactions());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getAverageCreditAmount());
        assertTrue(response.getCreditsByCategory().isEmpty());
        assertTrue(response.getTransactionCountByCategory().isEmpty());
    }

    @Test
    void getDebitTransactionsDashboard_WithEmptyTransactions_ShouldReturnEmptyDashboard() {
        // Act
        DashboardDebitResponse response = dashboardService.getDebitTransactionsDashboard(Collections.emptyList());

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getTotalDebitAmount());
        assertEquals(0, response.getTotalDebitTransactions());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getAverageDebitAmount());
        assertTrue(response.getDebitsByCategory().isEmpty());
        assertTrue(response.getTransactionCountByCategory().isEmpty());
    }

    @Test
    void getCreditTransactionsDashboard_WithEmptyTransactions_ShouldReturnEmptyDashboard() {
        // Act
        DashboardCreditResponse response = dashboardService.getCreditTransactionsDashboard(Collections.emptyList());

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getTotalCreditAmount());
        assertEquals(0, response.getTotalCreditTransactions());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), response.getAverageCreditAmount());
        assertTrue(response.getCreditsByCategory().isEmpty());
        assertTrue(response.getTransactionCountByCategory().isEmpty());
    }

    @Test
    void generateDashboards_ShouldProcessBothDashboards() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(
                debitTransaction1, debitTransaction2,
                creditTransaction1, creditTransaction2
        );

        // Act
        dashboardService.generateDashboards(transactions);

        // No assertions needed as the method is void and processDashboards is empty
        // This test is for coverage purposes
    }

    @Test
    void getDebitTransactionsDashboard_WithNullCategoryAndAmount_ShouldHandleGracefully() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(
                debitTransaction1,
                nullCategoryTransaction,
                nullAmountTransaction
        );

        // Act
        DashboardDebitResponse response = dashboardService.getDebitTransactionsDashboard(transactions);

        // Assert
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), response.getTotalDebitAmount());
        assertEquals(1, response.getTotalDebitTransactions());
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), response.getAverageDebitAmount());

        Map<String, BigDecimal> debitsByCategory = response.getDebitsByCategory();
        assertEquals(1, debitsByCategory.size());
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), debitsByCategory.get("Food"));

        Map<String, Long> countByCategory = response.getTransactionCountByCategory();
        assertEquals(1, countByCategory.size());
        assertEquals(1L, countByCategory.get("Food"));
    }

    @Test
    void getCreditTransactionsDashboard_WithNullCategoryAndAmount_ShouldHandleGracefully() {
        // Arrange
        Transaction nullCategoryCreditTransaction = Transaction.builder()
                .amount(BigDecimal.valueOf(75))
                .status(TransactionStatus.COMPLETED)
                .build();

        List<Transaction> transactions = Arrays.asList(
                creditTransaction1,
                nullCategoryCreditTransaction,
                nullAmountTransaction
        );

        // Act
        DashboardCreditResponse response = dashboardService.getCreditTransactionsDashboard(transactions);

        // Assert
        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), response.getTotalCreditAmount());
        assertEquals(1, response.getTotalCreditTransactions());
        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), response.getAverageCreditAmount());

        Map<String, BigDecimal> creditsByCategory = response.getCreditsByCategory();
        assertEquals(1, creditsByCategory.size());
        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), creditsByCategory.get("Food"));

        Map<String, Long> countByCategory = response.getTransactionCountByCategory();
        assertEquals(1, countByCategory.size());
        assertEquals(1L, countByCategory.get("Food"));
    }

    @Test
    void getDebitTransactionsDashboard_WithNonCompletedStatus_ShouldExcludeTransactions() {
        // Arrange
        Transaction pendingTransaction = Transaction.builder()
                .amount(BigDecimal.valueOf(-75))
                .category(category1)
                .status(TransactionStatus.NEW)
                .build();

        List<Transaction> transactions = Arrays.asList(debitTransaction1, pendingTransaction);

        // Act
        DashboardDebitResponse response = dashboardService.getDebitTransactionsDashboard(transactions);

        // Assert
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), response.getTotalDebitAmount());
        assertEquals(1, response.getTotalDebitTransactions());
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), response.getAverageDebitAmount());

        Map<String, BigDecimal> debitsByCategory = response.getDebitsByCategory();
        assertEquals(1, debitsByCategory.size());
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), debitsByCategory.get("Food"));

        Map<String, Long> countByCategory = response.getTransactionCountByCategory();
        assertEquals(1, countByCategory.size());
        assertEquals(1L, countByCategory.get("Food"));
    }

    @Test
    void getCreditTransactionsDashboard_WithNonCompletedStatus_ShouldExcludeTransactions() {
        // Arrange
        Transaction pendingTransaction = Transaction.builder()
                .amount(BigDecimal.valueOf(75))
                .category(category1)
                .status(TransactionStatus.NEW)
                .build();

        List<Transaction> transactions = Arrays.asList(creditTransaction1, pendingTransaction);

        // Act
        DashboardCreditResponse response = dashboardService.getCreditTransactionsDashboard(transactions);

        // Assert
        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), response.getTotalCreditAmount());
        assertEquals(1, response.getTotalCreditTransactions());
        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), response.getAverageCreditAmount());

        Map<String, BigDecimal> creditsByCategory = response.getCreditsByCategory();
        assertEquals(1, creditsByCategory.size());
        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), creditsByCategory.get("Food"));

        Map<String, Long> countByCategory = response.getTransactionCountByCategory();
        assertEquals(1, countByCategory.size());
        assertEquals(1L, countByCategory.get("Food"));
    }
} 