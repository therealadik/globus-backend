package com.example.globus.service.transaction;

import com.example.globus.dto.transaction.TransactionFilterDTO;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.transaction.filter.TransactionFilterSpecificationStrategy;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final List<TransactionFilterSpecificationStrategy> filterStrategies;

    /**
     * Получает список транзакций, отфильтрованных по заданным критериям и
     * принадлежащих текущему аутентифицированному пользователю.
     *
     * @param filter DTO с параметрами фильтрации.
     * @return Список транзакций, принадлежащих текущему пользователю и соответствующих фильтрам.
     */
    public List<Transaction> getTransactionsByFilter(TransactionFilterDTO filter) {
        // Получаем текущего пользователя
        String currentUsername = getCurrentUsername();
        if (currentUsername == null) {
            return List.of(); // Если пользователь не аутентифицирован, возвращаем пустой список
        }

        // Создаем базовую спецификацию для фильтрации по пользователю
        Specification<Transaction> spec = createUserSpecification(currentUsername);

        // Если есть фильтры, добавляем их к спецификации
        if (filter != null && filter.isAnyFilterSet()) {
            spec = spec.and(createFilterSpecification(filter));
        }

        // Выполняем запрос
        return transactionRepository.findAll(spec);
    }

    /**
     * Создает спецификацию для фильтрации транзакций по имени пользователя.
     *
     * @param username Имя пользователя.
     * @return Спецификация для фильтрации по пользователю.
     */
    private Specification<Transaction> createUserSpecification(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdBy").get("username"), username);
    }

    /**
     * Создает спецификацию на основе всех применимых фильтров.
     *
     * @param filter DTO с параметрами фильтрации.
     * @return Спецификация, объединяющая все применимые предикаты.
     */
    private Specification<Transaction> createFilterSpecification(TransactionFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Применяем все поддерживаемые стратегии фильтрации
            for (TransactionFilterSpecificationStrategy strategy : filterStrategies) {
                if (strategy.supports(filter)) {
                    predicates.add(strategy.createPredicate(filter, root, criteriaBuilder));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Получает имя текущего аутентифицированного пользователя.
     *
     * @return Имя пользователя или null, если пользователь не аутентифицирован.
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return authentication.getName();
    }

    // Здесь можно добавить другие методы для работы с транзакциями:
    // - createTransaction
    // - updateTransaction
    // - deleteTransaction
    // и т.д.
}
