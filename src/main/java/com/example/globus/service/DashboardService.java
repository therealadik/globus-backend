package com.example.globus.service;

import com.example.globus.dto.dashboard.BankTransactionStatisticsDto;
import com.example.globus.dto.dashboard.DebitCreditTransactionsDto;
import com.example.globus.dto.dashboard.IncomeExpenseComparisonDto;
import com.example.globus.dto.dashboard.TransactionCategoryStatsDto;
import com.example.globus.dto.dashboard.TransactionCountDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.mapstruct.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionMapper transactionMapper;

    public TransactionCountDto calculateTransactionCounts(List<Transaction> transactions) {
        int completedCount = 0;
        int canceledCount = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getStatus() == TransactionStatus.COMPLETED) {
                completedCount++;
            } else if (transaction.getStatus() == TransactionStatus.CANCELED) {
                canceledCount++;
            }
        }

        return new TransactionCountDto(completedCount, canceledCount);
    }

    public DebitCreditTransactionsDto calculateDebitCreditTransactions(List<Transaction> transactions) {
        List<TransactionResponseDto> debitTransactions = transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.EXPENSE)
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());

        List<TransactionResponseDto> creditTransactions = transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.INCOME)
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());

        return new DebitCreditTransactionsDto(debitTransactions, creditTransactions);
    }

    public IncomeExpenseComparisonDto calculateIncomeExpenseComparison(List<Transaction> transactions) {
        BigDecimal incomeAmount = BigDecimal.ZERO;
        BigDecimal expenseAmount = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                incomeAmount = incomeAmount.add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                expenseAmount = expenseAmount.add(transaction.getAmount());
            }
        }

        return IncomeExpenseComparisonDto.builder()
                .incomeAmount(incomeAmount)
                .expenseAmount(expenseAmount)
                .build();
    }

    public List<BankTransactionStatisticsDto> calculateBankStatistics(List<Transaction> transactions) {
        Map<String, Map<String, Long>> bankStats = new HashMap<>();

        for (Transaction transaction : transactions) {
            String senderBank = transaction.getBankSender().getName();
            String receiverBank = transaction.getBankReceiver().getName();

            bankStats.computeIfAbsent(senderBank, k -> new HashMap<>())
                    .merge(receiverBank, 1L, Long::sum);
        }

        return bankStats.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .map(receiverEntry -> new BankTransactionStatisticsDto(
                                entry.getKey(),
                                receiverEntry.getKey(),
                                receiverEntry.getValue()
                        )))
                .collect(Collectors.toList());
    }

    public TransactionCategoryStatsDto calculateTransactionCategoryStats(List<Transaction> transactions) {
        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> expenseByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                incomeByCategory.merge(categoryName, transaction.getAmount(), BigDecimal::add);
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                expenseByCategory.merge(categoryName, transaction.getAmount(), BigDecimal::add);
            }
        }

        return new TransactionCategoryStatsDto(incomeByCategory, expenseByCategory);
    }

    public byte[] generateCategoryReportPdf(List<Transaction> transactions) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Transaction Categories Report");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                contentStream.endText();

                Map<String, BigDecimal> incomeByCategory = new HashMap<>();
                Map<String, BigDecimal> expenseByCategory = new HashMap<>();

                for (Transaction transaction : transactions) {
                    String categoryName = transaction.getCategory().getName();
                    if (transaction.getTransactionType() == TransactionType.INCOME) {
                        incomeByCategory.merge(categoryName, transaction.getAmount(), BigDecimal::add);
                    } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                        expenseByCategory.merge(categoryName, transaction.getAmount(), BigDecimal::add);
                    }
                }

                float y = 680;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Income Categories:");
                contentStream.endText();

                y -= 20;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                for (Map.Entry<String, BigDecimal> entry : incomeByCategory.entrySet()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(String.format("%s: %s", entry.getKey(), entry.getValue()));
                    contentStream.endText();
                    y -= 20;
                }

                y -= 20;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Expense Categories:");
                contentStream.endText();

                y -= 20;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                for (Map.Entry<String, BigDecimal> entry : expenseByCategory.entrySet()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(String.format("%s: %s", entry.getKey(), entry.getValue()));
                    contentStream.endText();
                    y -= 20;
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
