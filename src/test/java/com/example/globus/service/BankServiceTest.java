package com.example.globus.service;

import com.example.globus.entity.Bank;
import com.example.globus.repository.BankRepository;
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
public class BankServiceTest {

    @InjectMocks
    private BankService bankService;

    @Mock
    private BankRepository bankRepository;

    @Test
    public void findByName_success() {
        Bank bank = new Bank();
        bank.setId(1L);
        bank.setName("Sberbank");

        when(bankRepository.findByName(bank.getName())).thenReturn(Optional.of(bank));
        Bank founded = bankService.findByName(bank.getName());
        assertEquals(bank, founded);
    }

    @Test
    public void findByName_failure() {
        Bank bank = new Bank();
        bank.setId(1L);
        bank.setName("Sberbank");

        when(bankRepository.findByName(bank.getName())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bankService.findByName(bank.getName()));
        assertTrue(ex.getMessage().contains("Банк с именем " + bank.getName() + " не найден"));
    }
}
