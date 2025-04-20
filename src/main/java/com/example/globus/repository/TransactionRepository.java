package com.example.globus.repository;

import com.example.globus.dto.dashboard.BankTransactionCountDto;
import com.example.globus.entity.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "SELECT new com.example.globus.dto.dashboard.BankTransactionCountDto(bSender.name AS senderBankName, bReceiver.name AS receiverBankName, COUNT(*) AS transactionCount )"
            + " FROM Transaction t "
            + " JOIN t.bankSender bSender "
            + " JOIN t.bankReceiver bReceiver "
            + " GROUP BY bSender.name, bReceiver.name "
            + " ORDER BY transactionCount DESC ")
    List<BankTransactionCountDto> findTransactionCountsByBanks();
}