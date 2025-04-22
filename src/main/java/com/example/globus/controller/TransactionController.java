package com.example.globus.controller;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.dto.transaction.UpdateTransactionRequestDto;
import com.example.globus.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @DeleteMapping("/{id}/")
    public void deleteTransaction(@Valid @Positive @PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}