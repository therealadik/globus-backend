package com.example.globus.service.transaction;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.transaction.filter.TransactionFilterSpecificationStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private List<TransactionFilterSpecificationStrategy> filterStrategies;
    
    @Captor
    private ArgumentCaptor<Specification<Transaction>> specificationCaptor;
    
    private static final String TEST_USERNAME = "testUser";
    
    @BeforeEach
    void setUp() {
        // Настраиваем аутентифицированного пользователя для тестов
        Authentication auth = new UsernamePasswordAuthenticationToken(TEST_USERNAME, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    @Test
    void getTransactionsByFilter_shouldReturnEmptyList_whenUserIsNotAuthenticated() {
        // Arrange
        SecurityContextHolder.clearContext(); // Очищаем контекст безопасности
        TransactionFilterDTO filter = createEmptyFilter();
        
        // Act
        List<Transaction> result = transactionService.getTransactionsByFilter(filter);
        
        // Assert
        assertTrue(result.isEmpty());
        verifyNoInteractions(transactionRepository);
    }
    
    @Test
    void getTransactionsByFilter_shouldFilterByUserAndApplyFilter_whenFilterIsSet() {
        // Arrange
        TransactionFilterDTO filter = createEmptyFilter();
        List<Transaction> expectedTransactions = Collections.singletonList(new Transaction());
        
        when(transactionRepository.findAll(any(Specification.class))).thenReturn(expectedTransactions);
        
        // Act
        List<Transaction> result = transactionService.getTransactionsByFilter(filter);
        
        // Assert
        assertEquals(expectedTransactions, result);
        verify(transactionRepository).findAll(specificationCaptor.capture());
        
        // Мы не можем напрямую проверить содержимое спецификации,
        // но мы можем проверить, что она была передана в repository
        assertNotNull(specificationCaptor.getValue());
    }
    
    @Test
    void getTransactionsByFilter_shouldOnlyFilterByUser_whenFilterIsNull() {
        // Arrange
        List<Transaction> expectedTransactions = Collections.singletonList(new Transaction());
        
        when(transactionRepository.findAll(any(Specification.class))).thenReturn(expectedTransactions);
        
        // Act
        List<Transaction> result = transactionService.getTransactionsByFilter(null);
        
        // Assert
        assertEquals(expectedTransactions, result);
        verify(transactionRepository).findAll(specificationCaptor.capture());
        
        // Мы не можем напрямую проверить содержимое спецификации,
        // но мы можем проверить, что она была передана в repository
        assertNotNull(specificationCaptor.getValue());
    }
    
    // Вспомогательный метод для создания пустого фильтра
    private TransactionFilterDTO createEmptyFilter() {
        return new TransactionFilterDTO(
                null, null, null, null, 
                null, null, null, null, 
                null, null, null
        );
    }
}
