package com.example.globus.mapstruct;

import com.example.globus.dto.RegistrationRequestDto;
import com.example.globus.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "role", constant = "USER")
    User toEntity(RegistrationRequestDto registrationRequestDto);
}