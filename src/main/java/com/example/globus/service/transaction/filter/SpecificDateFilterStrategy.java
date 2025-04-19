package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public class SpecificDateFilterStrategy implements TransactionFilterSpecificationStrategy {

    @Override
    public boolean supports(TransactionFilterDTO filter) {
        // Эта стратегия активна только если specificDate указана
        return filter.specificDate() != null; // Используем аксессор рекорда
    }

    @Override
    public Predicate createPredicate(TransactionFilterDTO filter, Root<Transaction> root, CriteriaBuilder cb) {
        // Используем аксессор рекорда, но сохраняем логику выбора всех транзакций за указанный день
        return cb.between(root.get("transactionDate"),
                filter.specificDate().atStartOfDay(),
                filter.specificDate().plusDays(1).atStartOfDay().minusNanos(1));
    }
}
