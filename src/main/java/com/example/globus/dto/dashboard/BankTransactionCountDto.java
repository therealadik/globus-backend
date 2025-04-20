package com.example.globus.dto.dashboard;

import lombok.Getter;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(senderBankName, receiverBankName, transactionCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BankTransactionCountDto that = (BankTransactionCountDto) obj;
        return Objects.equals(transactionCount, that.transactionCount) &&
                Objects.equals(senderBankName, that.senderBankName) &&
                Objects.equals(receiverBankName, that.receiverBankName);
    }
}