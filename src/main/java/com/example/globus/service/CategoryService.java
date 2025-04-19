package com.example.globus.service;

import com.example.globus.entity.Category;
import com.example.globus.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.repository = categoryRepository;
    }

    public Category findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Категория с именем " + name + " не найдена"));
    }
}
