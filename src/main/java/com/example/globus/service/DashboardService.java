package com.example.globus.service;

import com.example.globus.dto.dashboard.BankTransactionCountDto;
import com.example.globus.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public List<BankTransactionCountDto> calculateBankStatistics()
    {
        return transactionRepository.findTransactionCountsByBanks();
    }
}
