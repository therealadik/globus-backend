package com.example.globus.service;

import com.example.globus.dto.dashboard.BankTransactionStatisticsDto;
import com.example.globus.dto.dashboard.DebitCreditTransactionsDto;
import com.example.globus.dto.dashboard.IncomeExpenseComparisonDto;
import com.example.globus.dto.dashboard.TransactionCategoryStatsDto;
import com.example.globus.dto.dashboard.TransactionCountDto;
import com.example.globus.entity.Bank;
import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.mapstruct.TransactionMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void calculateTransactionCounts_ReturnsCorrectCounts() {
        List<Transaction> transactions = Arrays.asList(
                Transaction.builder()
                        .status(TransactionStatus.COMPLETED)
                        .build(),
                Transaction.builder()
                        .status(TransactionStatus.COMPLETED)
                        .build(),
                Transaction.builder()
                        .status(TransactionStatus.CANCELED)
                        .build()
        );

        TransactionCountDto result = dashboardService.calculateTransactionCounts(transactions);

        assertThat(result.completedCount()).isEqualTo(2);
        assertThat(result.canceledCount()).isEqualTo(1);
    }

    @Test
    void calculateDebitCreditTransactions_ReturnsCorrectTransactions() {
        List<Transaction> transactions = Arrays.asList(
                Transaction.builder()
                        .transactionType(TransactionType.EXPENSE)
                        .amount(new BigDecimal("100.00"))
                        .build(),
                Transaction.builder()
                        .transactionType(TransactionType.INCOME)
                        .amount(new BigDecimal("200.00"))
                        .build()
        );

        DebitCreditTransactionsDto result = dashboardService.calculateDebitCreditTransactions(transactions);

        assertThat(result.debitTransactions()).hasSize(1);
        assertThat(result.creditTransactions()).hasSize(1);
    }

    @Test
    void calculateIncomeExpenseComparison_ReturnsCorrectComparison() {
        List<Transaction> transactions = Arrays.asList(
                Transaction.builder()
                        .transactionType(TransactionType.EXPENSE)
                        .amount(new BigDecimal("100.00"))
                        .build(),
                Transaction.builder()
                        .transactionType(TransactionType.INCOME)
                        .amount(new BigDecimal("200.00"))
                        .build()
        );

        IncomeExpenseComparisonDto result = dashboardService.calculateIncomeExpenseComparison(transactions);

        assertThat(result.incomeAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(result.expenseAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void calculateBankStatistics_ReturnsCorrectStatistics() {
        Bank bank1 = Bank.builder().name("Bank1").build();
        Bank bank2 = Bank.builder().name("Bank2").build();
        Bank bank3 = Bank.builder().name("Bank3").build();

        List<Transaction> transactions = Arrays.asList(
                Transaction.builder()
                        .bankSender(bank1)
                        .bankReceiver(bank2)
                        .build(),
                Transaction.builder()
                        .bankSender(bank1)
                        .bankReceiver(bank3)
                        .build()
        );

        List<BankTransactionStatisticsDto> result = dashboardService.calculateBankStatistics(transactions);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).senderBankName()).isEqualTo("Bank1");
        assertThat(result.get(0).receiverBankName()).isEqualTo("Bank2");
        assertThat(result.get(0).transactionCount()).isEqualTo(1);
    }

    @Test
    void calculateTransactionCategoryStats_ReturnsCorrectStats() {
        Category category1 = Category.builder().name("Category1").build();
        Category category2 = Category.builder().name("Category2").build();

        List<Transaction> transactions = Arrays.asList(
                Transaction.builder()
                        .transactionType(TransactionType.EXPENSE)
                        .amount(new BigDecimal("100.00"))
                        .category(category1)
                        .build(),
                Transaction.builder()
                        .transactionType(TransactionType.INCOME)
                        .amount(new BigDecimal("200.00"))
                        .category(category2)
                        .build()
        );

        TransactionCategoryStatsDto result = dashboardService.calculateTransactionCategoryStats(transactions);

        assertThat(result.incomeByCategory()).containsEntry("Category2", new BigDecimal("200.00"));
        assertThat(result.expenseByCategory()).containsEntry("Category1", new BigDecimal("100.00"));
    }

    @Test
    void generateCategoryReportPdf_ReturnsValidPdf() throws IOException {
        Category category1 = Category.builder().name("Category1").build();
        Category category2 = Category.builder().name("Category2").build();

        List<Transaction> transactions = Arrays.asList(
                Transaction.builder()
                        .transactionType(TransactionType.EXPENSE)
                        .amount(new BigDecimal("100.00"))
                        .category(category1)
                        .build(),
                Transaction.builder()
                        .transactionType(TransactionType.INCOME)
                        .amount(new BigDecimal("200.00"))
                        .category(category2)
                        .build()
        );

        byte[] pdfBytes = dashboardService.generateCategoryReportPdf(transactions);

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            assertThat(document.getNumberOfPages()).isEqualTo(1);
        }
    }
}
