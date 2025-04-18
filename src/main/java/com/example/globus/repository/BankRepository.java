package com.example.globus.repository;

import com.example.globus.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
    @Query("SELECT b FROM Bank b WHERE LOWER(b.name) = LOWER(:name)")
    Optional<Bank> findByName(@Param("name")  String name);
}
