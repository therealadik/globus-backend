package com.example.globus.controller;

import com.example.globus.dto.transaction.NewRequestDto;
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

    @PostMapping("/create")
    public ResponseDto create(@Valid @RequestBody NewRequestDto request) {
        return new ResponseDto(transactionService.create(request));
    }
}
