package com.example.globus.service.transaction;

import com.example.globus.dto.transaction.TransactionFilterDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.service.transaction.filter.TransactionFilterSpecificationStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionFilterService {

    private final List<TransactionFilterSpecificationStrategy> filterStrategies;

    public Specification<Transaction> createSpecification(TransactionFilterDto filter) {
        return (Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = filterStrategies.stream()
                    .filter(s -> s.supports(filter))
                    .map(s -> s.createPredicate(filter, root, cb))
                    .toList();

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 