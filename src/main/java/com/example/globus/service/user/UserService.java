package com.example.globus.service.user;

import com.example.globus.dto.RegistrationRequestDto;
import com.example.globus.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    boolean existsByUsername(String username);
    User findByUsername(String username);
    User createUser(RegistrationRequestDto request);
    User getUser();
}
