package com.example.globus.service.transaction.filter;

import com.example.globus.dto.transaction.TransactionFilterDto;
import com.example.globus.entity.transaction.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Интерфейс для стратегий создания Predicate для фильтрации транзакций.
 */
public interface TransactionFilterSpecificationStrategy {

    /**
     * Проверяет, применима ли данная стратегия к указанному фильтру.
     * @param filter DTO с параметрами фильтрации.
     * @return true, если стратегия применима, иначе false.
     */
    boolean supports(TransactionFilterDto filter);

    /**
     * Создает Predicate на основе параметров фильтра.
     * @param filter DTO с параметрами фильтрации.
     * @param root Корневой объект запроса.
     * @param criteriaBuilder Построитель критериев.
     * @return Predicate для добавления к общему запросу.
     */
    Predicate createPredicate(TransactionFilterDto filter, Root<Transaction> root, CriteriaBuilder criteriaBuilder);
}
