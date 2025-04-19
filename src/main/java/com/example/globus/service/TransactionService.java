package com.example.globus.service;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionMapper transactionMapper;
    private final CategoryService categoryService;
    private final BankService bankService;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public boolean create(NewTransactionRequestDto transaction) {
        Transaction entity = transactionMapper.toEntity(transaction, userService.getUser(), bankService, categoryService);
        entity.setId(null);
        entity.setTransactionDate(LocalDateTime.now());
        entity.setStatus(TransactionStatus.NEW);
        transactionRepository.save(entity);
        return true;
    }
}
