package com.example.globus.service;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.entity.Bank;
import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.entity.user.User;
import com.example.globus.mapstruct.TransactionMapper;
import com.example.globus.repository.CategoryRepository;
import com.example.globus.repository.TransactionRepository;
import com.example.globus.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;


    @Mock
    private CategoryRepository categoryRepository;

    @Test
    public void findByName_success() {
        Category category = new Category();
        category.setId(1L);
        category.setName("TestCategory");

        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));
        Category founded = categoryService.findByName(category.getName());
        assertEquals(category, founded);
    }

    @Test
    public void findByName_failure() {
        Category category = new Category();
        category.setId(1L);
        category.setName("TestCategory");

        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> categoryService.findByName(category.getName()));
        assertTrue(ex.getMessage().contains("Категория с именем " + category.getName() + " не найдена"));
    }
}
