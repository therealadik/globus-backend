package com.example.globus.service;

import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int DECIMAL_PLACES = 2;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 20;
    private static final float SECTION_SPACING = 30;
    private static final float TITLE_FONT_SIZE = 16;
    private static final float HEADER_FONT_SIZE = 12;
    private static final float CONTENT_FONT_SIZE = 10;

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
                .filter(t -> t.getCategory() != null && t.getCategory().getName() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.mapping(
                                Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().abs().setScale(DECIMAL_PLACES, RoundingMode.HALF_UP)));

        Map<String, Long> transactionCountByCategory = debitTransactions.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().getName() != null)
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
                .filter(t -> t.getCategory() != null && t.getCategory().getName() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.mapping(
                                Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().setScale(DECIMAL_PLACES, RoundingMode.HALF_UP)));

        Map<String, Long> transactionCountByCategory = creditTransactions.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().getName() != null)
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

    public byte[] generateFinancialReport(List<Transaction> transactions) {
        if (transactions == null) {
            log.warn("Attempted to generate financial report with null transactions");
            return new byte[0];
        }

        try {
            DashboardDebitResponse debitDashboard = getDebitTransactionsDashboard(transactions);
            DashboardCreditResponse creditDashboard = getCreditTransactionsDashboard(transactions);

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Set up graphics state for transparency
                    PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                    graphicsState.setNonStrokingAlphaConstant(0.8f);
                    contentStream.setGraphicsStateParameters(graphicsState);

                    // Title
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, TITLE_FONT_SIZE);
                    contentStream.setNonStrokingColor(Color.DARK_GRAY);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, 750);
                    contentStream.showText("Financial Report");
                    contentStream.endText();

                    // Date
                    contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
                    contentStream.setNonStrokingColor(Color.GRAY);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, 720);
                    contentStream.showText("Generated: " + LocalDateTime.now().format(DATE_FORMATTER));
                    contentStream.endText();

                    float y = 680;

                    // Credit Transactions Section
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
                    contentStream.setNonStrokingColor(Color.DARK_GRAY);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText("Credit Transactions");
                    contentStream.endText();

                    y -= LINE_HEIGHT;
                    contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
                    contentStream.setNonStrokingColor(Color.BLACK);

                    // Draw table header
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText("Category");
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText("Amount");
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText("Count");
                    contentStream.endText();

                    y -= LINE_HEIGHT;
                    contentStream.setNonStrokingColor(Color.GRAY);
                    contentStream.moveTo(MARGIN, y);
                    contentStream.lineTo(MARGIN + 400, y);
                    contentStream.stroke();

                    y -= LINE_HEIGHT;

                    // Credit transactions
                    for (Map.Entry<String, BigDecimal> entry : creditDashboard.getCreditsByCategory().entrySet()) {
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText(entry.getKey());
                        contentStream.newLineAtOffset(200, 0);
                        contentStream.showText(entry.getValue().toString());
                        contentStream.newLineAtOffset(100, 0);
                        contentStream.showText(creditDashboard.getTransactionCountByCategory().get(entry.getKey()).toString());
                        contentStream.endText();
                        y -= LINE_HEIGHT;
                    }

                    y -= SECTION_SPACING;

                    // Debit Transactions Section
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
                    contentStream.setNonStrokingColor(Color.DARK_GRAY);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText("Debit Transactions");
                    contentStream.endText();

                    y -= LINE_HEIGHT;
                    contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
                    contentStream.setNonStrokingColor(Color.BLACK);

                    // Draw table header
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText("Category");
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText("Amount");
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText("Count");
                    contentStream.endText();

                    y -= LINE_HEIGHT;
                    contentStream.setNonStrokingColor(Color.GRAY);
                    contentStream.moveTo(MARGIN, y);
                    contentStream.lineTo(MARGIN + 400, y);
                    contentStream.stroke();

                    y -= LINE_HEIGHT;

                    // Debit transactions
                    for (Map.Entry<String, BigDecimal> entry : debitDashboard.getDebitsByCategory().entrySet()) {
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText(entry.getKey());
                        contentStream.newLineAtOffset(200, 0);
                        contentStream.showText(entry.getValue().toString());
                        contentStream.newLineAtOffset(100, 0);
                        contentStream.showText(debitDashboard.getTransactionCountByCategory().get(entry.getKey()).toString());
                        contentStream.endText();
                        y -= LINE_HEIGHT;
                    }

                    y -= SECTION_SPACING;

                    // Totals Section
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
                    contentStream.setNonStrokingColor(Color.DARK_GRAY);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText("Summary");
                    contentStream.endText();

                    y -= LINE_HEIGHT;
                    contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
                    contentStream.setNonStrokingColor(Color.BLACK);

                    // Draw summary table
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText("Total Credit:");
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText(creditDashboard.getTotalCreditAmount().toString());
                    contentStream.endText();

                    y -= LINE_HEIGHT;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText("Total Debit:");
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText(debitDashboard.getTotalDebitAmount().toString());
                    contentStream.endText();

                    y -= LINE_HEIGHT;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText("Net Balance:");
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText(creditDashboard.getTotalCreditAmount()
                            .subtract(debitDashboard.getTotalDebitAmount())
                            .toString());
                    contentStream.endText();
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                document.save(outputStream);
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            log.error("Error generating financial report", e);
            throw new RuntimeException("Failed to generate financial report", e);
        }
    }
}
