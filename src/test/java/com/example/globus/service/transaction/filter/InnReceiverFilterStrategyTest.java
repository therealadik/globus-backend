package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InnReceiverFilterStrategyTest {

    @InjectMocks
    private InnReceiverFilterStrategy strategy;

    @Mock
    private Root<Transaction> root;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> innReceiverPath;

    @Test
    void supports_shouldReturnTrue_whenInnReceiverIsNotNullAndNotBlank() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, "1234567890", null, 
                null, null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertTrue(result);
    }

    @Test
    void supports_shouldReturnFalse_whenInnReceiverIsNull() {
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
    void supports_shouldReturnFalse_whenInnReceiverIsBlank() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, "", null, 
                null, null, null
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertFalse(result);
    }

    @Test
    void createPredicate_shouldCreateEqualPredicate() {
        // Arrange
        String inn = "1234567890";
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, inn, null, 
                null, null, null
        );

        when(root.get("innReceiver")).thenReturn(innReceiverPath);
        when(criteriaBuilder.equal(innReceiverPath, inn))
                .thenReturn(mock(jakarta.persistence.criteria.Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).equal(innReceiverPath, inn);
    }
}
