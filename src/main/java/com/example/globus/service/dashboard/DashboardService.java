package com.example.globus.service.dashboard;

import com.example.globus.dto.dashboard.MonthlyTransactionCountDTO;
import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionService transactionService;

    /**
     * Расчет динамики количества транзакций по месяцам/годам на основе фильтра.
     * @param filter Фильтры для транзакций (может быть null или пустым для всех транзакций).
     * @return Список DTO с количеством транзакций по месяцам.
     */
    public List<MonthlyTransactionCountDTO> getMonthlyTransactionCounts(TransactionFilterDTO filter) {
        // Получаем отфильтрованные транзакции
        List<Transaction> transactions = transactionService.getTransactionsByFilter(filter);

        // Группируем по году и месяцу и считаем количество
        Map<Integer, Map<Month, Long>> countsByYearMonth = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getYear(),
                        Collectors.groupingBy(
                                t -> t.getTransactionDate().getMonth(),
                                Collectors.counting()
                        )
                ));

        // Преобразуем в список DTO
        return countsByYearMonth.entrySet().stream()
                .flatMap(yearEntry -> yearEntry.getValue().entrySet().stream()
                        .map(monthEntry -> new MonthlyTransactionCountDTO( // Используем конструктор рекорда
                                yearEntry.getKey(),
                                monthEntry.getKey().getValue(), // Получаем номер месяца (1-12)
                                monthEntry.getValue()
                        )))
                .sorted((d1, d2) -> { // Сортируем по году, затем по месяцу
                    int yearCompare = Integer.compare(d1.year(), d2.year()); // Используем аксессоры рекорда
                    if (yearCompare != 0) {
                        return yearCompare;
                    }
                    return Integer.compare(d1.month(), d2.month()); // Используем аксессоры рекорда
                })
                .collect(Collectors.toList());
    }

    // Здесь будут добавлены методы для других дашбордов
}
