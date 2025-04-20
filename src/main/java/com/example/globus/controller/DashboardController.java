package com.example.globus.controller;

import com.example.globus.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/calculateBankStatistics")
    public List<Object> calculateBankStatistics() {
        return Collections.singletonList(dashboardService.calculateBankStatistics());
    }
}
