package com.example.globus.dto.transaction;
import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NewTransactionRequestDto(@NotNull PersonType personType,
                                       @NotNull TransactionType transactionType,
                                       @NotNull @Min(value = 1, message = "amount должно быть >= 1") BigDecimal amount,
                                       @NotNull @NotBlank String bankSender,
                                       @NotNull @NotBlank String bankReceiver,
                                       @NotNull @NotBlank String innReceiver,
                                       @NotNull @NotBlank String accountReceiver,
                                       @NotNull @NotBlank String category,
                                       @NotNull @NotBlank String phoneReceiver
        ) {
}
