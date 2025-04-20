package com.example.globus.dto.dashboard;

import lombok.Getter;

public class BankTransactionCountDto {

    @Getter
    private String senderBankName;

    @Getter
    private String receiverBankName;

    @Getter
    private Long transactionCount;

    public BankTransactionCountDto(String senderBankName, String receiverBankName, Long transactionCount) {
        this.senderBankName = senderBankName;
        this.receiverBankName = receiverBankName;
        this.transactionCount = transactionCount;
    }
}