package com.example.globus.service;

import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionService transactionService;
    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 20;
    private static final float TITLE_FONT_SIZE = 24;
    private static final float HEADER_FONT_SIZE = 16;
    private static final float BODY_FONT_SIZE = 12;

    public byte[] generatePdfReport() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return generateCategoryReportPdf(transactions);
    }

    public byte[] generateCategoryReportPdf(List<Transaction> transactions) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Загрузка шрифта с поддержкой кириллицы
            PDType0Font font;
            try (InputStream fontStream = getClass().getResourceAsStream("/fonts/arial.ttf")) {
                if (fontStream == null) {
                    throw new RuntimeException("Шрифт не найден");
                }
                font = PDType0Font.load(document, fontStream);
            }

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Установка фона
                contentStream.setNonStrokingColor(0.94f, 0.94f, 0.94f); // Светло-серый
                contentStream.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                contentStream.fill();

                // Заголовок
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.setFont(font, TITLE_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, 750);
                contentStream.showText("Финансовый отчёт");
                contentStream.endText();

                // Дата генерации
                contentStream.setFont(font, BODY_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, 720);
                contentStream.showText("Сгенерировано: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                contentStream.endText();

                // Разделение на доходы и расходы
                Map<String, BigDecimal> incomeByCategory = new HashMap<>();
                Map<String, BigDecimal> expenseByCategory = new HashMap<>();
                BigDecimal totalIncome = BigDecimal.ZERO;
                BigDecimal totalExpense = BigDecimal.ZERO;

                for (Transaction transaction : transactions) {
                    String categoryName = transaction.getCategory().getName();
                    if (transaction.getTransactionType() == TransactionType.INCOME) {
                        incomeByCategory.merge(categoryName, transaction.getAmount(), BigDecimal::add);
                        totalIncome = totalIncome.add(transaction.getAmount());
                    } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                        expenseByCategory.merge(categoryName, transaction.getAmount(), BigDecimal::add);
                        totalExpense = totalExpense.add(transaction.getAmount());
                    }
                }

                float y = 680;

                // Доходы
                contentStream.setNonStrokingColor(0, 0.4f, 0); // Зеленый цвет для доходов
                contentStream.setFont(font, HEADER_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Доходы");
                contentStream.endText();

                y -= LINE_HEIGHT;
                contentStream.setFont(font, BODY_FONT_SIZE);
                for (Map.Entry<String, BigDecimal> entry : incomeByCategory.entrySet()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText(String.format("%s: %,.2f ₽", entry.getKey(), entry.getValue()));
                    contentStream.endText();
                    y -= LINE_HEIGHT;
                }

                // Итог по доходам
                contentStream.setFont(font, BODY_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText(String.format("Итого доходов: %,.2f ₽", totalIncome));
                contentStream.endText();

                y -= LINE_HEIGHT * 2;

                // Расходы
                contentStream.setNonStrokingColor(0.6f, 0, 0); // Красный цвет для расходов
                contentStream.setFont(font, HEADER_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Расходы");
                contentStream.endText();

                y -= LINE_HEIGHT;
                contentStream.setFont(font, BODY_FONT_SIZE);
                for (Map.Entry<String, BigDecimal> entry : expenseByCategory.entrySet()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(MARGIN, y);
                    contentStream.showText(String.format("%s: %,.2f ₽", entry.getKey(), entry.getValue()));
                    contentStream.endText();
                    y -= LINE_HEIGHT;
                }

                // Итог по расходам
                contentStream.setFont(font, BODY_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText(String.format("Итого расходов: %,.2f ₽", totalExpense));
                contentStream.endText();

                y -= LINE_HEIGHT * 2;

                // Чистый доход
                BigDecimal netIncome = totalIncome.subtract(totalExpense);
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.setFont(font, HEADER_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText(String.format("Чистый доход: %,.2f ₽", netIncome));
                contentStream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации PDF отчёта", e);
        }
    }
}
