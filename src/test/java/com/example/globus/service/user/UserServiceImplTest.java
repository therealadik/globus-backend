package com.example.globus.service.user;

import com.example.globus.dto.authentication.RegistrationRequestDto;
import com.example.globus.entity.user.User;
import com.example.globus.mapstruct.UserMapper;
import com.example.globus.mapstruct.UserMapperImpl;
import com.example.globus.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void loadUserByUsername_existing() {
        String username = "john";
        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername(username);
        assertSame(user, details);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_notFound() {
        String username = "nope";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(username)
        );
        assertTrue(ex.getMessage().contains(username));
    }

    @Test
    void createUser_success() {
        RegistrationRequestDto req = new RegistrationRequestDto("alice", "pass");

        User saved = User.builder().username("alice").build();

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.createUser(req);
        assertSame(saved, result);

        InOrder ord = inOrder(userRepository, userMapper);
        ord.verify(userRepository).existsByUsername("alice");
        ord.verify(userMapper).toEntity(req);
        ord.verify(userRepository).save(any(User.class));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User passedToSave = captor.getValue();
        assertEquals("alice", passedToSave.getUsername());
    }

    @Test
    void createUser_alreadyExists() {
        RegistrationRequestDto req = new RegistrationRequestDto("bob", "pw");
        when(userRepository.existsByUsername("bob")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.createUser(req));
        verify(userRepository).existsByUsername("bob");
    }
}