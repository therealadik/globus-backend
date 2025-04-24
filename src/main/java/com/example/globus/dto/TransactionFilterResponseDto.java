package com.example.globus.dto;

import com.example.globus.dto.dashboard.BankTransactionStatisticsDto;
import com.example.globus.dto.dashboard.DebitCreditTransactionsDto;
import com.example.globus.dto.dashboard.IncomeExpenseComparisonDto;
import com.example.globus.dto.dashboard.TransactionCategoryStatsDto;
import com.example.globus.dto.dashboard.TransactionCountByPeriodDto;
import com.example.globus.dto.dashboard.TransactionCountDto;

import java.util.List;

public record TransactionFilterResponseDto(
        List<BankTransactionStatisticsDto> bankTransactionStatistics,
        DebitCreditTransactionsDto debitCreditTransactions,
        IncomeExpenseComparisonDto incomeExpenseComparison,
        TransactionCategoryStatsDto transactionCategoryStats,
        TransactionCountByPeriodDto transactionCountByPeriod,
        TransactionCountDto transactionCount
) {
}
