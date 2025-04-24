package com.example.globus.service;


import com.example.globus.entity.Bank;
import com.example.globus.repository.BankRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    @Transactional(readOnly = true)
    public Bank findByName(String name) {
        return bankRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Банк с именем " + name + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }
}
