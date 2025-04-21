package com.example.globus.controller;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.dto.transaction.UpdateTransactionRequestDto;
import com.example.globus.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для создания, обновления и отмены транзакций.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public TransactionResponseDto create(
            @Valid @RequestBody NewTransactionRequestDto request
    ) {
        return transactionService.create(request);
    }

    @PutMapping
    public TransactionResponseDto updateTransaction(
            @Valid @RequestBody UpdateTransactionRequestDto request
    ) {
        return transactionService.updateTransaction(request);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            // cancelTransaction сам получает пользователя из UserService
            TransactionResponseDto dto = transactionService.cancelTransaction(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}