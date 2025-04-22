package com.example.globus.dto.dashboard;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record IncomeExpenseComparisonDto(
        BigDecimal incomeAmount,
        BigDecimal expenseAmount) {
}
