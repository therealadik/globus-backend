package com.example.globus.mapstruct;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.dto.transaction.TransactionResponseDto;
import com.example.globus.dto.transaction.UpdateTransactionRequestDto;
import com.example.globus.entity.transaction.Transaction;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    @Mapping(target = "bankSender.id", source = "bankSenderId")
    @Mapping(target = "bankReceiver.id", source = "bankReceiverId")
    @Mapping(target = "category.id", source = "categoryId")
    Transaction toEntity(NewTransactionRequestDto newTransactionRequestDto);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "bankReceiver.id", target = "bankReceiverId")
    @Mapping(source = "bankSender.id", target = "bankSenderId")
    TransactionResponseDto toDto(Transaction transaction);

    @Mapping(target = "bankSender.id", source = "bankSenderId")
    @Mapping(target = "bankReceiver.id", source = "bankReceiverId")
    @Mapping(target = "category.id", source = "categoryId")
    void updateEntityFromDto(UpdateTransactionRequestDto updateTransactionRequestDto, @MappingTarget Transaction transaction);

}