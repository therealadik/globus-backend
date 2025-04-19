package com.example.globus.service;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.entity.Bank;
import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.entity.user.User;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private BankService bankService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void create_transaction_success() {
        NewTransactionRequestDto newTransactionRequestDto = new NewTransactionRequestDto(
                PersonType.PHYSICAL,
                TransactionType.INCOME,
                new BigDecimal(7777),
                "Sberbank",
                "Sberbank",
                "123456",
                "accountReceiver",
                "TestCategory",
                "89086428563"
        );

        Bank bank = new Bank();
        bank.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(14L);

        User user = new User();
        user.setId(1L);

        when(userService.getUser()).thenReturn(user);
        when(transactionMapper.toEntity(
                newTransactionRequestDto, user, bankService, categoryService
        )).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        boolean result = transactionService.create(newTransactionRequestDto);
        assertTrue(result);
    }

    @Test
    public void create_transaction_failed() {
        NewTransactionRequestDto newTransactionRequestDto = new NewTransactionRequestDto(
                PersonType.PHYSICAL,
                TransactionType.INCOME,
                new BigDecimal(7777),
                "Sberbank",
                "Sberbank",
                "123456",
                "accountReceiver",
                "TestCategory",
                "89086428563"
        );

        Bank bank = new Bank();
        bank.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(14L);

        User user = new User();
        user.setId(1L);

        when(userService.getUser()).thenReturn(user);
        when(transactionMapper.toEntity(
                newTransactionRequestDto, user, bankService, categoryService
        )).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenThrow(new IllegalArgumentException("IllegalArgumentException"));

        RuntimeException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.create(newTransactionRequestDto));
        assertTrue(ex.getMessage().contains("IllegalArgumentException"));
    }
}
