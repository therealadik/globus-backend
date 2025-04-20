package com.example.globus.controller;

import com.example.globus.dto.transaction.TransactionResponseDTO;
import com.example.globus.entity.transaction.TransactionStatus;
import com.example.globus.entity.transaction.TransactionType;
import com.example.globus.service.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionCancelController.class)
class TransactionCancelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    @WithMockUser(username = "testuser")
    void cancelTransaction_ShouldReturnOk() throws Exception {
        TransactionResponseDTO mockResponse = new TransactionResponseDTO(
                1L,
                LocalDateTime.now(),
                TransactionType.DEBIT,
                new BigDecimal("500.00"),
                TransactionStatus.DELETED,
                "Bank A",
                "Bank B",
                "123456789012",
                "Utilities",
                "testuser"
        );

        Mockito.when(transactionService.cancelTransaction(1L, "testuser"))
                .thenReturn(mockResponse);

        mockMvc.perform(delete("/api/v1/transactions/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("DELETED"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void cancelTransaction_NotAllowed_ShouldReturnBadRequest() throws Exception {
        Mockito.when(transactionService.cancelTransaction(1L, "testuser"))
                .thenThrow(new IllegalStateException("You are not allowed to cancel this transaction."));

        mockMvc.perform(delete("/api/v1/transactions/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("You are not allowed to cancel this transaction."));
    }
}
