package com.example.globus.dto.transaction;

import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
    Long id,
    LocalDateTime transactionDate,
    TransactionType transactionType,
    BigDecimal amount,
    TransactionStatus status,
    String senderBankName, // Имя банка отправителя
    String receiverBankName, // Имя банка получателя
    String innReceiver,
    String categoryName, // Название категории
    String createdByUsername // Имя пользователя, создавшего транзакцию
) {
}
