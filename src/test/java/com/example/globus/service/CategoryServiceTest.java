package com.example.globus.service;

import com.example.globus.entity.Category;
import com.example.globus.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        assertTrue(ex.getMessage().contains("Категория с именем " + category.getName() + " не существует"));
    }
}
