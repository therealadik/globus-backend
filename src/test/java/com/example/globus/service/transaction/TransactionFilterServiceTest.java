package com.example.globus.service.transaction;

import com.example.globus.dto.transaction.TransactionFilterDto;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.service.transaction.filter.TransactionFilterSpecificationStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionFilterServiceTest {

    @Mock private TransactionFilterSpecificationStrategy strategy1;
    @Mock private TransactionFilterSpecificationStrategy strategy2;

    @Mock private Root<Transaction> root;
    @Mock private CriteriaQuery<?> query;
    @Mock private CriteriaBuilder cb;

    @Mock private Predicate predicate1;
    @Mock private Predicate predicate2;

    private TransactionFilterService service;

    @BeforeEach
    void setUp() {
        List<TransactionFilterSpecificationStrategy> strategies = Arrays.asList(strategy1, strategy2);
        service = new TransactionFilterService(strategies);
    }

    @Test
    void shouldReturnEmptyAndWhenNoStrategySupportsFilter() {
        // никакая стратегия не поддерживает фильтр
        TransactionFilterDto filter = mock(TransactionFilterDto.class);
        when(strategy1.supports(filter)).thenReturn(false);
        when(strategy2.supports(filter)).thenReturn(false);
        // любой массив предикатов -> predicate1
        when(cb.and(any(Predicate[].class))).thenReturn(predicate1);

        Specification<Transaction> spec = service.createSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate1, result);
        verify(strategy1).supports(filter);
        verify(strategy2).supports(filter);
        verify(strategy1, never()).createPredicate(any(), any(), any());
        verify(strategy2, never()).createPredicate(any(), any(), any());

        // словили пустой массив
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        Predicate[] actual = captor.getValue();
        assertEquals(0, actual.length);
    }

    @Test
    void shouldCombineSinglePredicate() {
        // только первая стратегия поддерживает
        TransactionFilterDto filter = mock(TransactionFilterDto.class);
        when(strategy1.supports(filter)).thenReturn(true);
        when(strategy2.supports(filter)).thenReturn(false);
        when(strategy1.createPredicate(filter, root, cb)).thenReturn(predicate1);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate1);

        Specification<Transaction> spec = service.createSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate1, result);
        verify(strategy1).supports(filter);
        verify(strategy2).supports(filter);
        verify(strategy1).createPredicate(filter, root, cb);
        verify(strategy2, never()).createPredicate(any(), any(), any());

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        Predicate[] actual = captor.getValue();
        assertArrayEquals(new Predicate[]{predicate1}, actual);
    }

    @Test
    void shouldCombineMultiplePredicates() {
        // обе стратегии поддерживают
        TransactionFilterDto filter = mock(TransactionFilterDto.class);
        when(strategy1.supports(filter)).thenReturn(true);
        when(strategy2.supports(filter)).thenReturn(true);
        when(strategy1.createPredicate(filter, root, cb)).thenReturn(predicate1);
        when(strategy2.createPredicate(filter, root, cb)).thenReturn(predicate2);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate1);

        Specification<Transaction> spec = service.createSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate1, result);
        verify(strategy1).supports(filter);
        verify(strategy2).supports(filter);
        verify(strategy1).createPredicate(filter, root, cb);
        verify(strategy2).createPredicate(filter, root, cb);

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        Predicate[] actual = captor.getValue();
        assertArrayEquals(new Predicate[]{predicate1, predicate2}, actual);
    }

    @Test
    void shouldHandleNullPredicatesAsEmptyElements() {
        // стратегии возвращают null
        TransactionFilterDto filter = mock(TransactionFilterDto.class);
        when(strategy1.supports(filter)).thenReturn(true);
        when(strategy2.supports(filter)).thenReturn(true);
        when(strategy1.createPredicate(filter, root, cb)).thenReturn(null);
        when(strategy2.createPredicate(filter, root, cb)).thenReturn(null);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate1);

        Specification<Transaction> spec = service.createSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate1, result);
        verify(strategy1).createPredicate(filter, root, cb);
        verify(strategy2).createPredicate(filter, root, cb);

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        Predicate[] actual = captor.getValue();
        assertEquals(2, actual.length);
        assertNull(actual[0]);
        assertNull(actual[1]);
    }

    @Test
    void shouldTreatNullFilterAsNoFilters() {
        // передали null-фильтр
        when(cb.and(any(Predicate[].class))).thenReturn(predicate1);

        Specification<Transaction> spec = service.createSpecification(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate1, result);
        verify(strategy1).supports(null);
        verify(strategy2).supports(null);
        verify(strategy1, never()).createPredicate(any(), any(), any());
        verify(strategy2, never()).createPredicate(any(), any(), any());

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        Predicate[] actual = captor.getValue();
        assertEquals(0, actual.length);
    }
}
