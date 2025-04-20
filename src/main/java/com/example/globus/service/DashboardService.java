package com.example.globus.service;

import com.example.globus.dto.dashboard.BankTransactionCountDto;
import com.example.globus.dto.transaction.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    public List<BankTransactionCountDto> calculateBankStatistics(List<TransactionDto> transactions) {
        Map<String, Long> groupedTransactions = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getBankSender() + "|" + t.getBankReceiver(),
                        Collectors.counting()
                ));

        return groupedTransactions.entrySet().stream()
                .map(entry -> {
                    String[] banks = entry.getKey().split("\\|" );
                    String senderBankName = banks[0];
                    String receiverBankName = banks[1];
                    long transactionCount = entry.getValue();
                    return new BankTransactionCountDto(senderBankName, receiverBankName, transactionCount);
                })
                .sorted(Comparator.comparingLong(BankTransactionCountDto::getTransactionCount).reversed())
                .collect(Collectors.toList());
    }
}
