package com.example.globus.controller;

import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionCancelController {

    private final TransactionService transactionService;

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<TransactionResponseDto> cancelTransaction(@PathVariable Long id, Authentication authentication) {
        try {
            boolean result = transactionService.cancelTransaction(id, authentication.getName());
            return ResponseEntity.ok(new TransactionResponseDto(result));
        } catch (IllegalStateException | EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(new TransactionResponseDto(false));
        }
    }
}
