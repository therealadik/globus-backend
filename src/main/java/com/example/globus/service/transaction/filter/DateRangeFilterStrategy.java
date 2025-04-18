package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateRangeFilterStrategy implements TransactionFilterSpecificationStrategy {

    @Override
    public boolean supports(TransactionFilterDTO filter) {
        // Используем только если не задана конкретная дата, но задан хотя бы один из параметров диапазона
        return filter.specificDate() == null && 
              (filter.dateFrom() != null || filter.dateTo() != null);
    }

    @Override
    public Predicate createPredicate(TransactionFilterDTO filter, Root<Transaction> root, CriteriaBuilder cb) {
        if (filter.dateFrom() != null && filter.dateTo() != null) {
            // Если заданы обе границы диапазона
            return cb.between(root.get("transactionDate"),
                    filter.dateFrom().atStartOfDay(),
                    filter.dateTo().plusDays(1).atStartOfDay().minusNanos(1));
        } else if (filter.dateFrom() != null) {
            // Если задана только нижняя граница
            return cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.dateFrom().atStartOfDay());
        } else {
            // Если задана только верхняя граница
            return cb.lessThan(root.get("transactionDate"), filter.dateTo().plusDays(1).atStartOfDay());
        }
    }
}
