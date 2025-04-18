package com.example.globus.service;

import com.example.globus.entity.Category;
import com.example.globus.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.repository = categoryRepository;
    }

    public Optional<Category> findBankByName(String name) {
        return repository.findByName(name);
    }
}
