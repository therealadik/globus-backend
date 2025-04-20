package com.example.globus.dto.transaction;

import lombok.Getter;

public class TransactionDto {

    @Getter
    private String bankSender;

    @Getter
    private String bankReceiver;

    public TransactionDto(String bankSender, String bankReceiver) {
        this.bankSender = bankSender;
        this.bankReceiver = bankReceiver;
    }
}