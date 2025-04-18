package com.example.globus.controller;

import com.example.globus.dto.dashboard.MonthlyTransactionCountDTO;
import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboards") // Базовый путь для дашбордов
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/monthly-counts")
    public ResponseEntity<List<MonthlyTransactionCountDTO>> getMonthlyTransactionCounts(
            TransactionFilterDTO filter // Принимаем те же фильтры, что и для списка транзакций
    ) {
        List<MonthlyTransactionCountDTO> monthlyCounts = dashboardService.getMonthlyTransactionCounts(filter);
        return ResponseEntity.ok(monthlyCounts);
    }

    // Здесь будут добавлены эндпоинты для других дашбордов
}
