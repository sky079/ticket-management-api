package com.ticket.ticketmanagement;

import com.ticket.ticketmanagement.entity.User;
import com.ticket.ticketmanagement.exception.EmailAlreadyExistsException;
import com.ticket.ticketmanagement.repository.UserRepository;
import com.ticket.ticketmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Sumit");
        testUser.setEmail("sumit@gmail.com");
        testUser.setPassword("hashedpassword");
        testUser.setRole(User.Role.USER);
    }

    @Test
    void registerUser_ShouldSaveUser_WhenEmailIsNew() {
        when(userRepository.existsByEmail("sumit@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser("Sumit", "sumit@gmail.com",
                "password123", User.Role.USER);

        assertNotNull(result);
        assertEquals("Sumit", result.getName());
        assertEquals("sumit@gmail.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("sumit@gmail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () ->
                userService.registerUser("Sumit", "sumit@gmail.com",
                        "password123", User.Role.USER));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void checkPassword_ShouldReturnTrue_WhenPasswordIsCorrect() {
        when(passwordEncoder.matches("password123", "hashedpassword")).thenReturn(true);

        boolean result = userService.checkPassword("password123", "hashedpassword");

        assertTrue(result);
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenPasswordIsWrong() {
        when(passwordEncoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        boolean result = userService.checkPassword("wrongpassword", "hashedpassword");

        assertFalse(result);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByEmail("sumit@gmail.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("sumit@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("sumit@gmail.com", result.get().getEmail());
    }
}