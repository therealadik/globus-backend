package com.example.globus.controller;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.service.TransactionService;
import jakarta.validation.Valid;
import com.example.globus.dto.transaction.UpdateTransactionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public TransactionResponseDto create(@Valid @RequestBody NewTransactionRequestDto request) {
        return transactionService.create(request);
    }

    @PutMapping
    public TransactionResponseDto updateTransaction (@Valid @RequestBody UpdateTransactionRequestDto transactionRequestDto){
        return transactionService.updateTransaction(transactionRequestDto);
    }
}
