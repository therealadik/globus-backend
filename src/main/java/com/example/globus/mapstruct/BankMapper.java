package com.example.globus.mapstruct;

import com.example.globus.dto.BankResponseDto;
import com.example.globus.entity.Bank;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BankMapper {
    BankResponseDto toBankDto(Bank bank);
}