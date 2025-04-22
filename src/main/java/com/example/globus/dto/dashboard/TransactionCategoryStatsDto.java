package com.example.globus.dto.dashboard;

import java.math.BigDecimal;
import java.util.Map;

public record TransactionCategoryStatsDto(
        Map<String, BigDecimal> incomeByCategory,
        Map<String, BigDecimal> expenseByCategory
) {
}
