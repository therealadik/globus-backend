package com.example.globus.service;


import com.example.globus.entity.Bank;
import com.example.globus.repository.BankRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankService {
    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public Bank findByName(String name) {
        return bankRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Банк с именем " + name + " не найден"));
    }
}
