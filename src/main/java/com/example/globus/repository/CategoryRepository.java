package com.example.globus.repository;

import com.example.globus.entity.Bank;
import com.example.globus.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT b FROM Category b WHERE LOWER(b.name) = LOWER(:name)")
    Optional<Category> findByName(@Param("name")  String name);
}
