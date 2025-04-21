package com.example.globus.service;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.dto.transaction.UpdateTransactionRequestDto;
import com.example.globus.entity.Bank;
import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.entity.user.User;
import com.example.globus.entity.user.UserRole;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Spy
    private TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void create_success() {
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
        when(userService.getAuthorizedUser()).thenReturn(mockUser);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            tx.setId(99L);
            tx.setCreatedBy(mockUser);
            return tx;
        });

        TransactionResponseDto result = transactionService.create(request);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(99L, result.id()),
                () -> assertEquals(request.amount(), result.amount()),
                () -> assertEquals(request.transactionDate(), result.transactionDate()),
                () -> assertEquals(request.phoneReceiver(), result.phoneReceiver())
        );

        verify(transactionMapper).toEntity(request);
        verify(userService).getAuthorizedUser();
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionMapper).toDto(any(Transaction.class));
    }

    @Test
    public void testUpdateTransaction() {
        UpdateTransactionRequestDto dto = new UpdateTransactionRequestDto(
                1L,
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

        User currentUser = new User();
        currentUser.setId(4L);
        currentUser.setRole(UserRole.USER);
        when(userService.getAuthorizedUser()).thenReturn(currentUser);

        Transaction tx = Transaction.builder()
                .id(1L)
                .transactionDate(LocalDateTime.parse("2025-10-01T12:00"))
                .personType(PersonType.PHYSICAL)
                .transactionType(TransactionType.EXPENSE)
                .amount(BigDecimal.valueOf(150.75))
                .status(TransactionStatus.NEW)
                .bankSender(new Bank())
                .bankReceiver(new Bank())
                .innReceiver("98765432109")
                .accountReceiver("OLD_ACC_123")
                .accountSender("OLD_ACC_456")
                .category(new Category())
                .phoneReceiver("+79999956789")
                .createdBy(currentUser)
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto result = transactionService.updateTransaction(dto);
        assertNotNull(result);
        assertEquals(dto.accountReceiver(), result.accountReceiver());
        assertEquals(dto.phoneReceiver(), result.phoneReceiver());
    }

    @Test
    public void testUpdateTransactionByAdmin() {
        UpdateTransactionRequestDto dto = new UpdateTransactionRequestDto(
                1L,
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

        User admin = new User();
        admin.setId(50L);
        admin.setRole(UserRole.ADMIN);
        when(userService.getAuthorizedUser()).thenReturn(admin);

        Transaction tx = Transaction.builder()
                .id(1L)
                .transactionDate(LocalDateTime.parse("2025-10-01T12:00"))
                .personType(PersonType.PHYSICAL)
                .transactionType(TransactionType.EXPENSE)
                .amount(BigDecimal.valueOf(150.75))
                .status(TransactionStatus.NEW)
                .bankSender(new Bank())
                .bankReceiver(new Bank())
                .innReceiver("98765432109")
                .accountReceiver("OLD_ACC_123")
                .accountSender("OLD_ACC_456")
                .category(new Category())
                .phoneReceiver("+79999956789")
                .createdBy(new User() {{ setId(4L); setRole(UserRole.USER); }})
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto result = transactionService.updateTransaction(dto);
        assertNotNull(result);
        assertEquals(dto.accountReceiver(), result.accountReceiver());
        assertEquals(dto.phoneReceiver(), result.phoneReceiver());
    }

    @Test
    public void testCancelTransaction_Success() {
        User user = new User();
        user.setId(1L);
        when(userService.getAuthorizedUser()).thenReturn(user);

        Transaction tx = Transaction.builder()
                .id(10L)
                .status(TransactionStatus.NEW)
                .transactionDate(LocalDateTime.now())
                .personType(PersonType.PHYSICAL)
                .transactionType(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .bankSender(new Bank())
                .bankReceiver(new Bank())
                .innReceiver("11111111111")
                .accountReceiver("ACC1")
                .accountSender("ACC2")
                .category(new Category())
                .phoneReceiver("+79999999999")
                .build();

        tx.setCreatedBy(user);
        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto result = transactionService.cancelTransaction(10L);
        assertEquals(TransactionStatus.DELETED, result.status());
        verify(transactionRepository).save(tx);
    }

    @Test
    public void testCancelTransaction_ForbiddenStatus() {
        User user = new User();
        user.setId(1L);
        when(userService.getAuthorizedUser()).thenReturn(user);

        Transaction tx = Transaction.builder()
                .id(20L)
                .status(TransactionStatus.COMPLETED)
                .createdBy(user)
                .build();
        when(transactionRepository.findById(20L)).thenReturn(Optional.of(tx));

        assertThrows(IllegalStateException.class, () -> transactionService.cancelTransaction(20L));
    }

    @Test
    public void testCancelTransaction_Unauthorized() {
        User user = new User();
        user.setId(1L);
        when(userService.getAuthorizedUser()).thenReturn(user);

        Transaction tx = Transaction.builder()
                .id(30L)
                .status(TransactionStatus.NEW)
                .createdBy(new User() {{ setId(2L); setRole(UserRole.USER); }})
                .build();
        when(transactionRepository.findById(30L)).thenReturn(Optional.of(tx));

        assertThrows(IllegalStateException.class, () -> transactionService.cancelTransaction(30L));
    }

    @Test
    public void testCancelTransaction_NotFound() {
        when(transactionRepository.findById(40L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> transactionService.cancelTransaction(40L));
    }
}
