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
class DateRangeFilterStrategyTest {

    @InjectMocks
    private DateRangeFilterStrategy strategy;

    @Mock
    private Root<Transaction> root;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> transactionDatePath;

    @Test
    void supports_shouldReturnFalse_whenSpecificDateIsSet() {
        // Arrange
        LocalDate specificDate = LocalDate.of(2025, 4, 18);
        LocalDate dateFrom = LocalDate.of(2025, 4, 1);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, specificDate, dateFrom, 
                null, null, null, null, 
                null, null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertFalse(result);
    }

    @Test
    void supports_shouldReturnTrue_whenDateFromIsSetAndSpecificDateIsNull() {
        // Arrange
        LocalDate dateFrom = LocalDate.of(2025, 4, 1);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, dateFrom, 
                null, null, null, null, 
                null, null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertTrue(result);
    }

    @Test
    void supports_shouldReturnTrue_whenDateToIsSetAndSpecificDateIsNull() {
        // Arrange
        LocalDate dateTo = LocalDate.of(2025, 4, 30);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                dateTo, null, null, null, 
                null, null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertTrue(result);
    }

    @Test
    void createPredicate_shouldCreateBetweenPredicate_whenBothDatesAreSet() {
        // Arrange
        LocalDate dateFrom = LocalDate.of(2025, 4, 1);
        LocalDate dateTo = LocalDate.of(2025, 4, 30);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, dateFrom, 
                dateTo, null, null, null, 
                null, null, null
        );

        when(root.get("transactionDate")).thenReturn(transactionDatePath);
        when(criteriaBuilder.between(
                eq(transactionDatePath),
                eq(dateFrom.atStartOfDay()),
                eq(dateTo.plusDays(1).atStartOfDay().minusNanos(1))
        )).thenReturn(mock(Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).between(
                transactionDatePath,
                dateFrom.atStartOfDay(),
                dateTo.plusDays(1).atStartOfDay().minusNanos(1)
        );
    }

    @Test
    void createPredicate_shouldCreateGreaterThanOrEqualPredicate_whenOnlyDateFromIsSet() {
        // Arrange
        LocalDate dateFrom = LocalDate.of(2025, 4, 1);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, dateFrom, 
                null, null, null, null, 
                null, null, null
        );

        when(root.get("transactionDate")).thenReturn(transactionDatePath);
        when(criteriaBuilder.greaterThanOrEqualTo(
                eq(transactionDatePath),
                eq(dateFrom.atStartOfDay())
        )).thenReturn(mock(Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).greaterThanOrEqualTo(
                transactionDatePath,
                dateFrom.atStartOfDay()
        );
    }

    @Test
    void createPredicate_shouldCreateLessThanPredicate_whenOnlyDateToIsSet() {
        // Arrange
        LocalDate dateTo = LocalDate.of(2025, 4, 30);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                dateTo, null, null, null, 
                null, null, null
        );

        when(root.get("transactionDate")).thenReturn(transactionDatePath);
        when(criteriaBuilder.lessThan(
                eq(transactionDatePath),
                eq(dateTo.plusDays(1).atStartOfDay())
        )).thenReturn(mock(Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).lessThan(
                transactionDatePath,
                dateTo.plusDays(1).atStartOfDay()
        );
    }
}
