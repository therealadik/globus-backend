package com.example.globus.mapper;

import com.example.globus.dto.transaction.TransactionResponseDTO;
import com.example.globus.entity.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring", // Интеграция со Spring
    unmappedTargetPolicy = ReportingPolicy.IGNORE, // Игнорировать немапленные поля
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE // Игнорировать null значения при маппинге
)
public interface TransactionMapper {

    @Mappings({
        @Mapping(source = "bankSender.name", target = "senderBankName"),
        @Mapping(source = "bankReceiver.name", target = "receiverBankName"),
        @Mapping(source = "category.name", target = "categoryName"),
        @Mapping(source = "createdBy.username", target = "createdByUsername")
    })
    TransactionResponseDTO toDto(Transaction transaction);

    List<TransactionResponseDTO> toDtoList(List<Transaction> transactions);

    // При необходимости можно добавить метод toEntity
}
