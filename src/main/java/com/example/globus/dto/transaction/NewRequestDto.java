package com.example.globus.dto.transaction;
import com.example.globus.entity.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NewRequestDto(@NotNull String personType,
                            @NotNull TransactionType transactionType,
                            @NotNull BigDecimal amount,
                            @NotNull @NotBlank String bankSender,
                            @NotNull @NotBlank String bankReceiver,
                            @NotNull @NotBlank String innReceiver,
                            @NotNull @NotBlank String accountReceiver,
                            @NotNull @NotBlank String category,
                            @NotNull @NotBlank String phoneReceiver
        ) {
}
