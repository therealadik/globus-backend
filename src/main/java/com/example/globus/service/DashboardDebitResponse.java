package com.example.globus.service;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DashboardDebitResponse {
    private BigDecimal totalDebitAmount;
    private long totalDebitTransactions;
    private Map<String, BigDecimal> debitsByCategory;
    private Map<String, Long> transactionCountByCategory;
    private BigDecimal averageDebitAmount;
} 