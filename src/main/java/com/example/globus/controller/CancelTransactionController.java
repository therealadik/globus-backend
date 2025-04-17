package com.example.globus.controller;

import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.repository.TransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контроллер для управления транзакциями.
 * Позволяет выполнять действия с транзакциями, такие как отмена.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Controller", description = "Управление транзакциями (отмена, подтверждение, удаление и т.п.)")
public class CancelTransactionController {

    private final TransactionRepository transactionRepository;

    /**
     * Отменяет транзакцию по её идентификатору.
     *
     * <p>Условия:</p>
     * <ul>
     *   <li>Если транзакция не найдена — возвращается 404 Not Found.</li>
     *   <li>Если транзакция уже отменена, удалена или завершена — возвращается 400 Bad Request.</li>
     *   <li>Если транзакция активна — её статус обновляется на {@code CANCELED}, и возвращается 200 OK.</li>
     * </ul>
     *
     * @param id идентификатор транзакции
     * @return HTTP-ответ с результатом операции
     */
    @Operation(
            summary = "Отменить транзакцию",
            description = "Устанавливает статус транзакции как 'CANCELED', если это допустимо."
    )
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);

        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction transaction = optionalTransaction.get();

        if (transaction.getStatus() == TransactionStatus.CANCELED ||
                transaction.getStatus() == TransactionStatus.DELETED ||
                transaction.getStatus() == TransactionStatus.COMPLETED) {
            return ResponseEntity.badRequest().body("Транзакция не может быть отменена.");
        }

        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);

        return ResponseEntity.ok("Транзакция успешно отменена.");
    }
}
