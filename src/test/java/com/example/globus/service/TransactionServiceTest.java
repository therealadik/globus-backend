package com.example.globus.service;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.entity.user.User;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.mapstruct.TransactionMapperImpl;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Spy
    private TransactionMapper transactionMapper = new TransactionMapperImpl();

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void create_success() {
        // Arrange
        NewTransactionRequestDto request = new NewTransactionRequestDto(
                PersonType.PHYSICAL,
                TransactionType.EXPENSE,
                LocalDateTime.parse("2023-10-01T12:00"),
                TransactionStatus.NEW,
                BigDecimal.valueOf(150.75),
                1L,
                2L,
                "12345678901",
                "ACC_123",
                "ACC_456",
                3L,
                "+79123456789"
        );

        User mockUser = new User();
        mockUser.setId(5L);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(99L);
        savedTransaction.setCreatedBy(mockUser);

        when(userService.getAuthorizedUser()).thenReturn(mockUser);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction entity = invocation.getArgument(0);
            entity.setId(99L);
            entity.setCreatedBy(mockUser);
            return entity;
        });

        // Act
        TransactionResponseDto result = transactionService.create(request);

        // Assert
        assertAll(
                () -> assertNotNull(result, "Ответ не должен быть null"),
                () -> assertEquals(99L, result.id(), "Неверный ID транзакции"),
                () -> assertEquals(mockUser.getId(), savedTransaction.getCreatedBy().getId(), "Создатель не совпадает"),
                () -> assertEquals(request.amount(), result.amount(), "Сумма не совпадает"),
                () -> assertEquals(request.transactionDate(), result.transactionDate(), "Дата транзакции не совпадает"),
                () -> assertEquals(request.phoneReceiver(), result.phoneReceiver(), "Телефон получателя не совпадает")
        );

        verify(transactionMapper).toEntity(request);
        verify(userService).getAuthorizedUser();
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionMapper).toDto(any(Transaction.class));
    }
}