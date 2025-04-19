package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public class AmountRangeFilterStrategy implements TransactionFilterSpecificationStrategy {

    @Override
    public boolean supports(TransactionFilterDTO filter) {
        return filter.amountFrom() != null || filter.amountTo() != null;
    }

    @Override
    public Predicate createPredicate(TransactionFilterDTO filter, Root<Transaction> root, CriteriaBuilder cb) {
        if (filter.amountFrom() != null && filter.amountTo() != null) {
            // Если заданы обе границы диапазона
            return cb.between(root.get("amount"), filter.amountFrom(), filter.amountTo());
        } else if (filter.amountFrom() != null) {
            // Если задана только нижняя граница
            return cb.greaterThanOrEqualTo(root.get("amount"), filter.amountFrom());
        } else {
            // Если задана только верхняя граница
            return cb.lessThanOrEqualTo(root.get("amount"), filter.amountTo());
        }
    }
}
