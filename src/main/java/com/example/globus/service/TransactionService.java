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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;

    private static final EnumSet<TransactionStatus> BLOCKED = EnumSet.of(
            TransactionStatus.CONFIRMED,
            TransactionStatus.PROCESSING,
            TransactionStatus.CANCELED,
            TransactionStatus.COMPLETED,
            TransactionStatus.RETURNED
    );

    @Transactional
    public TransactionResponseDto create(NewTransactionRequestDto dto) {
        Transaction tx = transactionMapper.toEntity(dto);
        tx.setCreatedBy(userService.getAuthorizedUser());
        tx = transactionRepository.save(tx);
        return transactionMapper.toDto(tx);
    }

    @Transactional
    public TransactionResponseDto updateTransaction(UpdateTransactionRequestDto dto) {
        Transaction tx = transactionRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Transaction " + dto.id() + " not found"));
        if (tx.getStatus() != TransactionStatus.NEW) {
            throw new IllegalStateException("Transaction status " + tx.getStatus() + " cannot be updated");
        }
        User current = userService.getAuthorizedUser();
        if (!tx.getCreatedBy().getId().equals(current.getId()) && current.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("No rights to update this transaction");
        }
        transactionMapper.updateEntityFromDto(dto, tx);
        tx.setUpdatedBy(current);
        tx = transactionRepository.save(tx);
        return transactionMapper.toDto(tx);
    }

    /**
     * Отменяет транзакцию: проверяет авторство и статус, меняет на CANCELED.
     * @param id ID транзакции
     * @return DTO с обновлённым статусом
     */
    @Transactional
    public TransactionResponseDto deleteTransaction(Long id) {
        Transaction tx = findById(id);
        User current = userService.getAuthorizedUser();
        validUserOwnerTransaction(tx, current);

        if (BLOCKED.contains(tx.getStatus())) {
            throw new IllegalStateException("Transaction status " + tx.getStatus() + " cannot be deleted");
        }

        tx.setStatus(TransactionStatus.DELETED);
        tx.setUpdatedBy(current);
        return transactionMapper.toDto(tx);
    }

    @Transactional(readOnly = true)
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction " + id + " not found"));
    }

    private void validUserOwnerTransaction(Transaction tx, User user) {
        if (!tx.getCreatedBy().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("You cannot cancel this transaction");
        }
    }
}