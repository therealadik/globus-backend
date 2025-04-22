package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDto;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public class InnReceiverFilterStrategy implements TransactionFilterSpecificationStrategy {

    @Override
    public boolean supports(TransactionFilterDto filter) {
        return filter.innReceiver() != null && !filter.innReceiver().isBlank();
    }

    @Override
    public Predicate createPredicate(TransactionFilterDto filter, Root<Transaction> root, CriteriaBuilder cb) {
        return cb.equal(root.get("innReceiver"), filter.innReceiver());
    }
}
