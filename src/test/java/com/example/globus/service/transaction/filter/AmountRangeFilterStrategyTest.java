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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmountRangeFilterStrategyTest {

    @InjectMocks
    private AmountRangeFilterStrategy strategy;

    @Mock
    private Root<Transaction> root;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<BigDecimal> amountPath;

    @Test
    void supports_shouldReturnTrue_whenAmountFromIsNotNull() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, BigDecimal.valueOf(100), 
                null, null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertTrue(result);
    }

    @Test
    void supports_shouldReturnTrue_whenAmountToIsNotNull() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, null, 
                BigDecimal.valueOf(500), null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertTrue(result);
    }

    @Test
    void supports_shouldReturnFalse_whenBothAmountBoundsAreNull() {
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
    void createPredicate_shouldCreateBetweenPredicate_whenBothBoundsAreSet() {
        // Arrange
        BigDecimal amountFrom = BigDecimal.valueOf(100);
        BigDecimal amountTo = BigDecimal.valueOf(500);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, amountFrom, 
                amountTo, null, null
        );

        when(root.get("amount")).thenReturn(amountPath);
        when(criteriaBuilder.between(eq(amountPath), eq(amountFrom), eq(amountTo)))
                .thenReturn(mock(Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).between(amountPath, amountFrom, amountTo);
    }

    @Test
    void createPredicate_shouldCreateGreaterThanOrEqualToPredicate_whenOnlyAmountFromIsSet() {
        // Arrange
        BigDecimal amountFrom = BigDecimal.valueOf(100);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, amountFrom, 
                null, null, null
        );

        when(root.get("amount")).thenReturn(amountPath);
        when(criteriaBuilder.greaterThanOrEqualTo(eq(amountPath), eq(amountFrom)))
                .thenReturn(mock(Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).greaterThanOrEqualTo(amountPath, amountFrom);
    }

    @Test
    void createPredicate_shouldCreateLessThanOrEqualToPredicate_whenOnlyAmountToIsSet() {
        // Arrange
        BigDecimal amountTo = BigDecimal.valueOf(500);
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, null, 
                amountTo, null, null
        );

        when(root.get("amount")).thenReturn(amountPath);
        when(criteriaBuilder.lessThanOrEqualTo(eq(amountPath), eq(amountTo)))
                .thenReturn(mock(Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).lessThanOrEqualTo(amountPath, amountTo);
    }
}
