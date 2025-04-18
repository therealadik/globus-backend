package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component // Регистрируем как Spring Bean
public class BankSenderFilterStrategy implements TransactionFilterSpecificationStrategy {

    @Override
    public boolean supports(TransactionFilterDTO filter) {
        return filter.bankSenderId() != null; // Используем аксессор рекорда вместо геттера
    }

    @Override
    public Predicate createPredicate(TransactionFilterDTO filter, Root<Transaction> root, CriteriaBuilder cb) {
        return cb.equal(root.get("bankSender").get("id"), filter.bankSenderId()); // Используем аксессор рекорда
    }
}
