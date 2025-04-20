package com.example.globus.service;

import com.example.globus.dto.dashboard.BankTransactionCountDto;
import com.example.globus.dto.transaction.TransactionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class DashboardServiceTest {

    @Test
    public void calculateBankStatistics_test() {
        DashboardService service = new DashboardService();
        List<TransactionDto> transactions = Arrays.asList(
                new TransactionDto("Тинькофф", "ВТБ"),
                new TransactionDto("Тинькофф", "ВТБ"),
                new TransactionDto("Сбербанк", "ВТБ"),
                new TransactionDto("Сбербанк", "ВТБ"),
                new TransactionDto("Сбербанк", "ВТБ"),
                new TransactionDto("Сбербанк", "ВТБ"),
                new TransactionDto("Сбербанк", "Сбербанк")
        );

        List<BankTransactionCountDto> expected = Arrays.asList(
                new BankTransactionCountDto("Сбербанк", "ВТБ", 4L),
                new BankTransactionCountDto("Тинькофф", "ВТБ", 2L),
                new BankTransactionCountDto("Сбербанк", "Сбербанк", 1L)
        );
        List<BankTransactionCountDto> actual = service.calculateBankStatistics(transactions);
        Assertions.assertEquals(expected, actual);
    }
}