package com.example.globus.service.auth;

import com.example.globus.dto.AuthenticationResponseDto;
import com.example.globus.dto.LoginRequestDto;
import com.example.globus.dto.RegistrationRequestDto;
import com.example.globus.security.JwtTokenProvider;
import com.example.globus.entity.user.User;
import com.example.globus.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Override
    @Transactional
    public AuthenticationResponseDto register(RegistrationRequestDto request) {
        User user = userService.createUser(request);
        String token = jwtTokenProvider.generateToken(user);
        return new AuthenticationResponseDto(token);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userService.findByUsername(request.username());

        String token = jwtTokenProvider.generateToken(user);
        return new AuthenticationResponseDto(token);
    }
}
