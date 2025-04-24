package com.example.globus.service.auth;

import com.example.globus.dto.authentication.AuthenticationResponseDto;
import com.example.globus.dto.authentication.LoginRequestDto;
import com.example.globus.dto.authentication.RegistrationRequestDto;

public interface AuthenticationService {
    AuthenticationResponseDto register(RegistrationRequestDto request);
    AuthenticationResponseDto login(LoginRequestDto request);
}
