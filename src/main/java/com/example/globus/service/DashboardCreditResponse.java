package com.example.globus.service;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DashboardCreditResponse {
    private BigDecimal totalCreditAmount;
    private long totalCreditTransactions;
    private Map<String, BigDecimal> creditsByCategory;
    private Map<String, Long> transactionCountByCategory;
    private BigDecimal averageCreditAmount;
} 