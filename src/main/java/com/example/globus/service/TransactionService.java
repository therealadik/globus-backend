package com.example.globus.service;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.dto.transaction.UpdateTransactionRequestDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.user.User;
import com.example.globus.entity.user.UserRole;
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

    @Transactional
    public TransactionResponseDto updateTransaction(UpdateTransactionRequestDto transactionRequestDto) {
        Transaction transaction = transactionRepository.getById(transactionRequestDto.id());

        if (transaction == null)
            throw new RuntimeException("Транзакция с " + transactionRequestDto.id() + "не найдена");
        if (!transaction.getStatus().equals(TransactionStatus.NEW))
            throw new RuntimeException("Транзакция не может быть изменена, её статус: " + transaction.getStatus());

        User currentUser = userService.getAuthorizedUser();

        if (transaction.getCreatedBy().getId().equals(currentUser.getId()) || currentUser.getRole().equals(UserRole.ADMIN)) {
            transactionMapper.updateEntityFromDto(transactionRequestDto, transaction);
            transaction.setUpdatedBy(currentUser);
            transaction = transactionRepository.save(transaction);
            return transactionMapper.toDto(transaction);
        } else throw new RuntimeException("У вас нет прав на редактирование данной транзакции");


    }
}
