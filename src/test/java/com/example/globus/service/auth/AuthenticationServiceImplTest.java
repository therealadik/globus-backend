package com.example.globus.service.auth;

import com.example.globus.dto.AuthenticationResponseDto;
import com.example.globus.dto.LoginRequestDto;
import com.example.globus.dto.RegistrationRequestDto;
import com.example.globus.entity.user.User;
import com.example.globus.security.JwtTokenProvider;
import com.example.globus.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthenticationServiceImpl authService;

    @Test
    void register_success() {
        RegistrationRequestDto req = new RegistrationRequestDto("eve", "secret");
        User newUser = new User();
        newUser.setUsername("eve");

        when(userService.createUser(req)).thenReturn(newUser);
        when(jwtTokenProvider.generateToken(newUser)).thenReturn("jwt-token");

        AuthenticationResponseDto resp = authService.register(req);
        assertEquals("jwt-token", resp.token());

        verify(userService).createUser(req);
        verify(jwtTokenProvider).generateToken(newUser);
    }

    @Test
    void login_success() {
        LoginRequestDto req = new LoginRequestDto("frank", "pwd");
        User found = User.builder().username("frank").build();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("frank", "pwd");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class))
        ).thenReturn(authToken);

        when(userService.findByUsername("frank")).thenReturn(found);
        when(jwtTokenProvider.generateToken(found)).thenReturn("token123");

        AuthenticationResponseDto resp = authService.login(req);
        assertEquals("token123", resp.token());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("frank", "pwd")
        );
        verify(userService).findByUsername("frank");
        verify(jwtTokenProvider).generateToken(found);
    }

    @Test
    void login_failure() {
        LoginRequestDto req = new LoginRequestDto("wrong", "creds");

        doThrow(new RuntimeException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertTrue(ex.getMessage().contains("Bad credentials"));
    }
}