package com.example.globus.dto;

import com.example.globus.dto.dashboard.BankTransactionStatisticsDto;
import com.example.globus.dto.dashboard.DebitCreditTransactionsDto;
import com.example.globus.dto.dashboard.IncomeExpenseComparisonDto;
import com.example.globus.dto.dashboard.TransactionCategoryStatsDto;
import com.example.globus.dto.dashboard.TransactionCountByPeriodDto;
import com.example.globus.dto.dashboard.TransactionCountDto;

public record TransactionFilterResponseDto(
        BankTransactionStatisticsDto bankTransactionStatistics,
        DebitCreditTransactionsDto debitCreditTransactions,
        IncomeExpenseComparisonDto incomeExpenseComparison,
        TransactionCategoryStatsDto transactionCategoryStats,
        TransactionCountByPeriodDto transactionCountByPeriod,
        TransactionCountDto transactionCount
) {
}
