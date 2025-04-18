package com.example.globus.controller;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.dto.transaction.TransactionResponseDTO;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.mapper.TransactionMapper;
import com.example.globus.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions") // Базовый путь для контроллера
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getFilteredTransactions(
            TransactionFilterDTO filter // Spring автоматически свяжет параметры запроса с полями DTO
    ) {
        List<Transaction> transactions = transactionService.getTransactionsByFilter(filter);
        List<TransactionResponseDTO> responseDTOs = transactionMapper.toDtoList(transactions);
        return ResponseEntity.ok(responseDTOs);
    }

    // Здесь можно добавить другие эндпоинты для работы с транзакциями (создание, обновление и т.д.)
}
