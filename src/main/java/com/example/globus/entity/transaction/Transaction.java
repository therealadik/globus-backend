package com.example.globus.entity.transaction;

import com.example.globus.entity.Bank;
import com.example.globus.entity.Category;
import com.example.globus.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

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
    @JoinColumn(name = "bank_sender_id", nullable = false)
    private Bank bankSender;

    @ManyToOne
    @JoinColumn(name = "bank_receiver_id", nullable = false)
    private Bank bankReceiver;

    @Pattern(regexp = "\\d{11}", message = "ИНН должен содержать ровно 11 цифр")
    @Column(name = "inn_receiver", nullable = false)
    private String innReceiver;

    @Column(name = "account_receiver", nullable = false)
    private String accountReceiver;

    @Column(name = "account_sender", nullable = false)
    private String accountSender;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "phone_receiver", nullable = false)
    @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Телефон должен начинаться с +7 или 8 и содержать 11 цифр")
    private String phoneReceiver;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}