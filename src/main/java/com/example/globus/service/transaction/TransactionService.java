package com.example.globus.service.transaction;

import com.example.globus.dto.TransactionFilterResponseDto;
import com.example.globus.dto.dashboard.*;
import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionFilterDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.dto.transaction.UpdateTransactionRequestDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.user.User;
import com.example.globus.entity.user.UserRole;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.DashboardService;
import com.example.globus.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;
    private final TransactionFilterService transactionFilterService;

    private static final EnumSet<TransactionStatus> BLOCKED = EnumSet.of(
            TransactionStatus.CONFIRMED,
            TransactionStatus.PROCESSING,
            TransactionStatus.CANCELED,
            TransactionStatus.COMPLETED,
            TransactionStatus.RETURNED
    );
    private final DashboardService dashboardService;

    @Transactional
    public TransactionResponseDto create(NewTransactionRequestDto dto) {
        Transaction tx = transactionMapper.toEntity(dto);
        tx.setCreatedBy(userService.getAuthorizedUser());
        tx = transactionRepository.save(tx);
        return transactionMapper.toDto(tx);
    }

    @Transactional
    public TransactionResponseDto updateTransaction(UpdateTransactionRequestDto dto) {
        Transaction tx = transactionRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Transaction " + dto.id() + " not found"));
        if (tx.getStatus() != TransactionStatus.NEW) {
            throw new IllegalStateException("Transaction status " + tx.getStatus() + " cannot be updated");
        }
        User current = userService.getAuthorizedUser();
        if (!tx.getCreatedBy().getId().equals(current.getId()) && current.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("No rights to update this transaction");
        }
        transactionMapper.updateEntityFromDto(dto, tx);
        tx.setUpdatedBy(current);
        tx = transactionRepository.save(tx);
        return transactionMapper.toDto(tx);
    }

    /**
     * Отменяет транзакцию: проверяет авторство и статус, меняет на CANCELED.
     *
     * @param id ID транзакции
     * @return DTO с обновлённым статусом
     */
    @Transactional
    public TransactionResponseDto deleteTransaction(Long id) {
        Transaction tx = findById(id);
        User current = userService.getAuthorizedUser();
        validUserOwnerTransaction(tx, current);

        if (BLOCKED.contains(tx.getStatus())) {
            throw new IllegalStateException("Transaction status " + tx.getStatus() + " cannot be deleted");
        }

        tx.setStatus(TransactionStatus.DELETED);
        tx.setUpdatedBy(current);
        return transactionMapper.toDto(tx);
    }

    @Transactional(readOnly = true)
    public TransactionFilterResponseDto findTransactionsByFilter(TransactionFilterDto filter) {
        Specification<Transaction> specification = transactionFilterService.createSpecification(filter);
        List<Transaction> transactions = transactionRepository.findAll(specification);

        TransactionCountDto transactionCountDto = dashboardService.calculateTransactionCounts(transactions);
        DebitCreditTransactionsDto debitCreditTransactionsDto = dashboardService.calculateDebitCreditTransactions(transactions);
        IncomeExpenseComparisonDto incomeExpenseComparisonDto = dashboardService.calculateIncomeExpenseComparison(transactions);
        List<BankTransactionStatisticsDto> bankTransactionStatisticsDtos = dashboardService.calculateBankStatistics(transactions);
        TransactionCategoryStatsDto transactionCategoryStatsDto = dashboardService.calculateTransactionCategoryStats(transactions);
        TransactionCountByPeriodDto transactionCountByPeriodDto = dashboardService.caclulateStatsPerPeriods(transactions, LocalDateTime.now());

        return new TransactionFilterResponseDto(
                bankTransactionStatisticsDtos,
                debitCreditTransactionsDto,
                incomeExpenseComparisonDto,
                transactionCategoryStatsDto,
                transactionCountByPeriodDto,
                transactionCountDto
        );
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction " + id + " not found"));
    }

    private void validUserOwnerTransaction(Transaction tx, User user) {
        if (!tx.getCreatedBy().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("You cannot cancel this transaction");
        }
    }
}