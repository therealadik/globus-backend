package com.example.globus.service;

import com.example.globus.dto.dashboard.DashboardTransactionCountDto;
import com.example.globus.dto.dashboard.DebitCreditTransactionsDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.mapstruct.TransactionMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Spy
    private TransactionMapper transactionMapper = new TransactionMapperImpl();

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void counts() {
        List<Transaction> list = List.of(
                Transaction.builder().status(TransactionStatus.NEW).build(),
                Transaction.builder().status(TransactionStatus.COMPLETED).build(),
                Transaction.builder().status(TransactionStatus.DELETED).build(),
                Transaction.builder().status(TransactionStatus.CANCELED).build()
        );
        DashboardTransactionCountDto dto = dashboardService.calculateTransactionCounts(list);
        assertEquals(2, dto.completedCount());
        assertEquals(2, dto.canceledCount());
    }

    @Test
    void calculateDebitCreditTransactions_Success(){
        Transaction incomeTx1 = new Transaction();
        incomeTx1.setTransactionType(TransactionType.INCOME);
        Transaction incomeTx2 = new Transaction();
        incomeTx2.setTransactionType(TransactionType.INCOME);
        Transaction expenseTx = new Transaction();
        expenseTx.setTransactionType(TransactionType.EXPENSE);

        List<Transaction> allTransactions = List.of(incomeTx1, expenseTx, incomeTx2);

        DebitCreditTransactionsDto result =
                dashboardService.calculateDebitCreditTransactions(allTransactions);

        assertNotNull(result.creditTransactions(), "Список creditTransactions не должен быть null");
        assertEquals(2, result.creditTransactions().size(), "Должно быть 2 кредитные транзакции");

        assertNotNull(result.debitTransactions(), "Список debitTransactions не должен быть null");
        assertEquals(1, result.debitTransactions().size(), "Должна быть 1 дебетовая транзакция");
    }
}
