package com.example.globus.dto.transaction;

import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.globus.entity.transaction.Transaction}
 */
public record TransactionResponseDto(Long id,
                                     LocalDateTime transactionDate,
                                     PersonType personType,
                                     TransactionType transactionType,
                                     BigDecimal amount,
                                     TransactionStatus status,
                                     Long bankSenderId,
                                     Long bankReceiverId,
                                     String innReceiver,
                                     String accountReceiver,
                                     String accountSender,
                                     Long categoryId,
                                     String phoneReceiver) {
}