package com.example.globus.service.dashboard;

import com.example.globus.dto.dashboard.MonthlyTransactionCountDTO;
import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.service.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private TransactionService transactionService;

    @Test
    void getMonthlyTransactionCounts_shouldReturnEmptyList_whenNoTransactions() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, null, 
                null, null, null
        );
        when(transactionService.getTransactionsByFilter(filter)).thenReturn(Collections.emptyList());

        // Act
        List<MonthlyTransactionCountDTO> result = dashboardService.getMonthlyTransactionCounts(filter);

        // Assert
        assertTrue(result.isEmpty());
        verify(transactionService).getTransactionsByFilter(filter);
    }

    @Test
    void getMonthlyTransactionCounts_shouldGroupTransactionsByYearAndMonth() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, null, 
                null, null, null
        );
        
        // Создаем тестовые транзакции с разными датами
        Transaction jan2025 = createTransactionWithDate(2025, 1, 15);
        Transaction feb2025 = createTransactionWithDate(2025, 2, 10);
        Transaction feb2025Another = createTransactionWithDate(2025, 2, 20);
        Transaction mar2024 = createTransactionWithDate(2024, 3, 5);
        
        when(transactionService.getTransactionsByFilter(filter)).thenReturn(
                Arrays.asList(jan2025, feb2025, feb2025Another, mar2024)
        );

        // Act
        List<MonthlyTransactionCountDTO> result = dashboardService.getMonthlyTransactionCounts(filter);

        // Assert
        assertEquals(3, result.size()); // Должно быть 3 различных месяца
        
        // Проверяем, что результаты сортированы по дате
        assertEquals(2024, result.get(0).year());
        assertEquals(3, result.get(0).month());
        assertEquals(1, result.get(0).transactionCount());
        
        assertEquals(2025, result.get(1).year());
        assertEquals(1, result.get(1).month());
        assertEquals(1, result.get(1).transactionCount());
        
        assertEquals(2025, result.get(2).year());
        assertEquals(2, result.get(2).month());
        assertEquals(2, result.get(2).transactionCount()); // Две транзакции в феврале 2025
        
        verify(transactionService).getTransactionsByFilter(filter);
    }
    
    @Test
    void getMonthlyTransactionCounts_shouldPassFilterToTransactionService() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                1L, 2L, null, null, 
                null, null, null, null, 
                null, null, null
        );
        when(transactionService.getTransactionsByFilter(filter)).thenReturn(Collections.emptyList());

        // Act
        dashboardService.getMonthlyTransactionCounts(filter);

        // Assert
        verify(transactionService).getTransactionsByFilter(filter);
    }
    
    // Вспомогательный метод для создания транзакции с указанной датой
    private Transaction createTransactionWithDate(int year, int month, int day) {
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDateTime.of(year, month, day, 0, 0));
        return transaction;
    }
}
