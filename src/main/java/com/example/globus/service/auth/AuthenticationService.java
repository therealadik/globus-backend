package com.example.globus.service.auth;

import com.example.globus.dto.AuthenticationResponseDto;
import com.example.globus.dto.LoginRequestDto;
import com.example.globus.dto.RegistrationRequestDto;

public interface AuthenticationService {
    AuthenticationResponseDto register(RegistrationRequestDto request);
    AuthenticationResponseDto login(LoginRequestDto request);
}
