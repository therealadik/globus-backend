package com.example.globus.service;

import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.service.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private ReportService reportService;

    @Test
    public void generatePdfReport_Success() {
        // Подготовка тестовых данных
        Category category1 = new Category();
        category1.setName("Зарплата");

        Category category2 = new Category();
        category2.setName("Продукты");

        Transaction income = new Transaction();
        income.setAmount(new BigDecimal("1000.00"));
        income.setTransactionType(TransactionType.INCOME);
        income.setCategory(category1);

        Transaction expense = new Transaction();
        expense.setAmount(new BigDecimal("500.00"));
        expense.setTransactionType(TransactionType.EXPENSE);
        expense.setCategory(category2);

        List<Transaction> transactions = List.of(income, expense);

        // Настройка мока
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Выполнение теста
        byte[] pdfBytes = reportService.generatePdfReport();

        // Проверка результата
        assertNotNull(pdfBytes, "PDF не должен быть null");
        assertTrue(pdfBytes.length > 0, "PDF должен содержать данные");
    }
} 