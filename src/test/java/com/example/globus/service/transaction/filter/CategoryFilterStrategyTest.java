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
class CategoryFilterStrategyTest {

    @InjectMocks
    private CategoryFilterStrategy strategy;

    @Mock
    private Root<Transaction> root;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> categoryPath;

    @Mock
    private Path<Object> idPath;

    @Test
    void supports_shouldReturnTrue_whenCategoryIdIsNotNull() {
        // Arrange
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, null, 
                null, null, 1L
        );

        // Act
        boolean result = strategy.supports(filter);

        // Assert
        assertTrue(result);
    }

    @Test
    void supports_shouldReturnFalse_whenCategoryIdIsNull() {
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
    void createPredicate_shouldCreateEqualPredicate() {
        // Arrange
        Long categoryId = 1L;
        TransactionFilterDTO filter = new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, null, 
                null, null, categoryId
        );

        when(root.get("category")).thenReturn(categoryPath);
        when(categoryPath.get("id")).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, categoryId))
                .thenReturn(mock(jakarta.persistence.criteria.Predicate.class));

        // Act
        strategy.createPredicate(filter, root, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).equal(idPath, categoryId);
    }
}
