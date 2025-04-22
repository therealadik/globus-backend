package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDto;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public class SpecificDateFilterStrategy implements TransactionFilterSpecificationStrategy {

    @Override
    public boolean supports(TransactionFilterDto filter) {
        return filter.specificDate() != null;
    }

    @Override
    public Predicate createPredicate(TransactionFilterDto filter, Root<Transaction> root, CriteriaBuilder cb) {
        return cb.between(root.get("transactionDate"),
                filter.specificDate().atStartOfDay(),
                filter.specificDate().plusDays(1).atStartOfDay().minusNanos(1));
    }
}
