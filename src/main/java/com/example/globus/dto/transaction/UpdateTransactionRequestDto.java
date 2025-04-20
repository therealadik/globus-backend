package com.example.globus.dto.transaction;

import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateTransactionRequestDto(@NotNull @Positive Long id,
                                          @NotNull PersonType personType,
                                          @NotNull TransactionType transactionType,
                                          @NotNull @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
                                          LocalDateTime transactionDate,
                                          @NotNull TransactionStatus status,
                                          @NotNull @Positive(message = "Сумма должна быть положительной") BigDecimal amount,
                                          @NotNull @Positive Long bankSenderId,
                                          @NotNull @Positive Long bankReceiverId,
                                          @Pattern(regexp = "\\d{11}", message = "ИНН должен содержать ровно 11 цифр")
                                          @NotNull @NotBlank String innReceiver,
                                          @NotNull @NotBlank String accountReceiver,
                                          @NotNull @NotBlank String accountSender,
                                          @NotNull @Positive Long categoryId,
                                          @NotNull @NotBlank @Pattern(regexp = "^\\+?7\\d{10}$", message = "Телефон должен начинаться с +7 или 8 и содержать 11 цифр")
                                          String phoneReceiver
) {
}
