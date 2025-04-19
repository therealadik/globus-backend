package com.example.globus.mapstruct;

import com.example.globus.dto.transaction.NewTransactionRequestDto;
import com.example.globus.entity.Bank;
import com.example.globus.entity.Category;
import com.example.globus.entity.transaction.PersonType;
import com.example.globus.entity.transaction.Transaction;
import com.example.globus.entity.user.User;
import com.example.globus.service.BankService;
import com.example.globus.service.CategoryService;
import org.mapstruct.*;

import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    @Mapping(source = "newRequestDto.bankSender", target = "bankSender")
    @Mapping(source = "newRequestDto.category", target = "category")
    @Mapping(source = "newRequestDto.personType", target = "personType")
    @Mapping(target = "createdBy", expression = "java(user)")
    @Mapping(target = "updatedBy", expression = "java(user)")
    Transaction toEntity(NewTransactionRequestDto newRequestDto, User user, @Context BankService bankService, @Context CategoryService categoryService);

    default Bank mapBank(String bankName, @Context BankService bankService) {
        return bankService.findByName(bankName);
    }

    default Category mapCategory(String categoryName, @Context CategoryService categoryService) {
        return categoryService.findByName(categoryName);
    }

    default PersonType mapPersonType(String personType) {
        if (personType == null) {
            return null;
        }
        try {
            return PersonType.valueOf(personType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid PersonType: " + personType);
        }
    }
}