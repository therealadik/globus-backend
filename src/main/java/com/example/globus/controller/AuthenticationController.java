package com.example.globus.controller;

import com.example.globus.dto.AuthenticationResponseDto;
import com.example.globus.dto.LoginRequestDto;
import com.example.globus.dto.RegistrationRequestDto;
import com.example.globus.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthenticationResponseDto register(@Valid @RequestBody RegistrationRequestDto request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public AuthenticationResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authenticationService.login(request);
    }
}
