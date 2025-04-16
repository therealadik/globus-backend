package com.example.globus.model.transaction;

import com.example.globus.model.Bank;
import com.example.globus.model.Category;
import com.example.globus.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_datetime", nullable = false)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false)
    private PersonType personType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name= "amount" ,nullable = false, precision = 15, scale = 5)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "bank_sender_id")
    private Bank bankSender;

    @ManyToOne
    @JoinColumn(name = "bank_receiver_id")
    private Bank bankReceiver;

    @Column(name = "inn_receiver", nullable = false, length = 12)
    private String innReceiver;

    @Column(name = "account_receiver", nullable = false)
    private String accountReceiver;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Pattern(
            regexp = "^(\\\\+7|8)\\\\d{10}$\"",
            message = "Телефон должен начинаться с +7 или 8 и содержать 10 цифр после"

    )
    private String phoneReceiver;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}