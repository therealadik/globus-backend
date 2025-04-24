package com.example.globus.dto.dashboard;

import com.example.globus.dto.transaction.TransactionResponseDto;

import java.util.List;

public record DebitCreditTransactionsDto(
        List<TransactionResponseDto> debitTransactions,
        List<TransactionResponseDto> creditTransactions) {
}
