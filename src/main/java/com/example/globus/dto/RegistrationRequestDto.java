package com.example.globus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * DTO for {@link com.example.globus.entity.user.User}
 */
public record RegistrationRequestDto(@NotNull @NotBlank @Length(max = 15) String username,
                                     @NotNull @NotBlank @Length(message = "Длина пароля минимум 8 символов", min = 8) String password) {
}