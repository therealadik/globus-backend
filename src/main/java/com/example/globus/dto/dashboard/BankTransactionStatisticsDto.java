package com.example.globus.dto.dashboard;

import com.example.globus.dto.BankResponseDto;

import java.util.List;

public record BankTransactionStatisticsDto(
        String senderBankName,
        String receiverBankName,
        Long transactionCount) {
}
