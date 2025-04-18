package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecificDateFilterStrategyTest {

    @InjectMocks
    private SpecificDateFilterStrategy strategy;

    @Mock
    private Root<Transaction> root;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> transactionDatePath;

    @Test
    void supports_shouldReturnTrue_whenSpecificDateIsNotNull() {
        // Arrange
        LocalDate specificDate = LocalDate.of(2025, 4, 18);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, specificDate, null, 
                null, null, null, null, 
                null, null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertTrue(result);
    }

    @Test
    void supports_shouldReturnFalse_whenSpecificDateIsNull() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, null, 
                null, null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertFalse(result);
    }

    @Test
    void createPredicate_shouldCreateBetweenPredicate() {
        // Arrange
        LocalDate today = LocalDate.of(2025, 4, 18);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, today, null, 
                null, null, null, null, 
                null, null, null
        );

        when(root.get("transactionDate")).thenReturn(transactionDatePath);
        
        // Ожидаем создание предиката between для диапазона "весь день"
        when(criteriaBuilder.between(
                eq(transactionDatePath),
                eq(today.atStartOfDay()),
                eq(today.plusDays(1).atStartOfDay().minusNanos(1))
        )).thenReturn(mock(Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).between(
                transactionDatePath,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay().minusNanos(1)
        );
    }
}
