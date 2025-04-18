package com.example.globus.controller;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.ResponseDto;
import com.example.globus.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping()
    public ResponseDto create(@Valid @RequestBody NewTransactionRequestDto request) {
        return new ResponseDto(transactionService.create(request));
    }
}
