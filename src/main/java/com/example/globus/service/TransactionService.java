package com.example.globus.service;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponseDto create(NewTransactionRequestDto requestDto) {
        Transaction transaction = transactionMapper.toEntity(requestDto);
        transaction.setCreatedBy(userService.getAuthorizedUser());

        transaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }
}
