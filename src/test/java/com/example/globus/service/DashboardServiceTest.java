package com.example.globus.service;

import com.example.globus.dto.dashboard.*;
import com.example.globus.entity.Bank;
import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.entity.user.User;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.mapstruct.TransactionMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
        TransactionCountDto dto = dashboardService.calculateTransactionCounts(list);
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

    @Test
    void calculateIncomeExpenseComparison_ShouldCalculateSumsCorrectly() {
        // Подготовка тестовых транзакций
        Transaction income1 = new Transaction();
        income1.setTransactionType(TransactionType.INCOME);
        income1.setAmount(new BigDecimal("100.50"));

        Transaction expense = new Transaction();
        expense.setTransactionType(TransactionType.EXPENSE);
        expense.setAmount(new BigDecimal("50.25"));

        Transaction income2 = new Transaction();
        income2.setTransactionType(TransactionType.INCOME);
        income2.setAmount(new BigDecimal("200.75"));

        List<Transaction> transactions = List.of(income1, expense, income2);

        // Вызов
        IncomeExpenseComparisonDto result = dashboardService.calculateIncomeExpenseComparison(transactions);

        // Проверки
        assertEquals(new BigDecimal("301.25"), result.incomeAmount(),
                "Сумма доходов должна быть 100.50 + 200.75 = 301.25");
        assertEquals(new BigDecimal("50.25"), result.expenseAmount(),
                "Сумма расходов должна быть 50.25");
    }

    @Test
    public void calculateBankStatistics_test() {
        LocalDateTime now = LocalDateTime.now();

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, now, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Тинькофф"),new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, now, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Тинькофф"),new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, now, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"),new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, now, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"),new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, now, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"),new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, now, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"),new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, now, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"),new Bank(1L, "Сбербанк"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User())
        );
        List<BankTransactionStatisticsDto> expected = Arrays.asList(
                new BankTransactionStatisticsDto("Сбербанк", "ВТБ", 4L),
                new BankTransactionStatisticsDto("Тинькофф", "ВТБ", 2L),
                new BankTransactionStatisticsDto("Сбербанк", "Сбербанк", 1L)
        );
        List<BankTransactionStatisticsDto> actual = dashboardService.calculateBankStatistics(transactions);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void caclulateStatsPerPeriodsTest() {

        /**
         *  long weeklyCount; (wc)
         *  long monthlyCount; (mc)
         *  long quarterlyCount; (qc)
         *  long yearlyCount0L; (yc)
         */

        //пятница
        //Квартал:3
        //mock
        LocalDateTime testDate = LocalDateTime.of(2024, 9, 27, 12, 12, 12);


        /**
         *  wc:1
         *  mc:1
         *  qc:1
         *  yc:1
         */
        LocalDateTime midnight = testDate.toLocalDate().atStartOfDay();

        /**
         *  wc:2
         *  mc:2
         *  qc:2
         *  yc:2
         */
        LocalDateTime startOfWeek = testDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();

        /**
         *  wc:2
         *  mc:3
         *  qc:3
         *  yc:3
         */
        LocalDateTime previousWeekEnd = startOfWeek.minusSeconds(1);

        /**
         *  wc:2
         *  mc:4
         *  qc:4
         *  yc:4
         */
        LocalDateTime startOfMonth = testDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        /**
         *  wc:2
         *  mc:4
         *  qc:5
         *  yc:5
         */
        LocalDateTime previousMonthLastDay = startOfMonth.minusSeconds(1);

        //Начала кварталов: 1 4 7 10

        /**
         *  wc:2
         *  mc:4
         *  qc:6
         *  yc:6
         */
        int currentQuarter = (testDate.getMonthValue() - 1) / 3 + 1;
        LocalDateTime startOfQuarter = LocalDateTime.of(
                testDate.getYear(),
                (currentQuarter - 1) * 3 + 1, // Первый месяц квартала (1, 4, 7, 10)
                1, 0, 0
        );

        /**
         *  wc:2
         *  mc:4
         *  qc:6
         *  yc:7
         */
        LocalDateTime previousQuarterLastDay = startOfQuarter.minusSeconds(1);


        /**
         *  wc:2
         *  mc:4
         *  qc:6
         *  yc:8
         */
        LocalDateTime startOfYear = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime previousYearLastDay = LocalDateTime.of(2023, 12, 31, 23, 59, 59);  // Конец года


        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, midnight, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Тинькофф"), new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, startOfWeek, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Тинькофф"), new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, previousWeekEnd, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"), new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, startOfMonth, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"), new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, previousMonthLastDay, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"), new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, startOfQuarter, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"), new Bank(1L, "ВТБ"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, previousQuarterLastDay, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"), new Bank(1L, "Сбербанк"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, startOfYear, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"), new Bank(1L, "Сбербанк"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User()),
                new Transaction(1L, previousYearLastDay, PersonType.PHYSICAL, TransactionType.INCOME, new BigDecimal(1000), TransactionStatus.NEW, new Bank(1L, "Сбербанк"), new Bank(1L, "Сбербанк"), "12345678912", "test", "test", new Category(), "79086428563", new User(), new User())
        );
            TransactionCountByPeriodDto caclulateStatsPerPeriods = dashboardService.caclulateStatsPerPeriods(transactions, testDate);

            assertEquals(caclulateStatsPerPeriods.weeklyCount(),2);
            assertEquals(caclulateStatsPerPeriods.monthlyCount(),4);
            assertEquals(caclulateStatsPerPeriods.quarterlyCount(), 6);
            assertEquals(caclulateStatsPerPeriods.yearlyCount(), 8);





    }
}
