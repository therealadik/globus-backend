package com.example.globus.dto.transaction;

import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionFilterDTO(
    Long bankSenderId,
    Long bankReceiverId,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate specificDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dateFrom,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dateTo,
    TransactionStatus status,
    String innReceiver, 
    BigDecimal amountFrom,
    BigDecimal amountTo,
    TransactionType transactionType, 
    Long categoryId 
) {
    // Вспомогательный метод для проверки, установлен ли хотя бы один фильтр
    public boolean isAnyFilterSet() {
        return bankSenderId != null || bankReceiverId != null ||
               specificDate != null || dateFrom != null || dateTo != null ||
               status != null || (innReceiver != null && !innReceiver.isBlank()) || 
               amountFrom != null || amountTo != null ||
               transactionType != null || categoryId != null;
    }
}
