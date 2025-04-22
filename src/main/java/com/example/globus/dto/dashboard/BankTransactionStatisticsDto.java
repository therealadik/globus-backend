package com.example.globus.dto.dashboard;

import com.example.globus.dto.BankResponseDto;

import java.util.List;

public record BankTransactionStatisticsDto(
        List<BankResponseDto> senderBanks,
        List<BankResponseDto> receiverBanks) {
}
