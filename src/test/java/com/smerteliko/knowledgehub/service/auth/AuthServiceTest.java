package com.smerteliko.knowledgehub.service.auth;

import com.smerteliko.knowledgehub.abstracts.AbstractTest;
import com.smerteliko.knowledgehub.dto.auth.JwtResponse;
import com.smerteliko.knowledgehub.dto.auth.LoginRequest;
import com.smerteliko.knowledgehub.dto.auth.RegisterRequest;
import com.smerteliko.knowledgehub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class AuthServiceTest extends AbstractTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("testuser");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("strongpassword123");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("strongpassword123");
    }

    @Test
    void testRegisterAndLoginSuccess() {
        JwtResponse registerResponse = authService.register(validRegisterRequest);

        assertNotNull(registerResponse.getToken());
        assertEquals("testuser", registerResponse.getUsername());
        assertNotNull(registerResponse.getUserId());

        assertTrue(userRepository.findByEmail(validRegisterRequest.getEmail()).isPresent());

        JwtResponse loginResponse = authService.login(validLoginRequest);

        assertNotNull(loginResponse.getToken());
        assertEquals(registerResponse.getUserId(), loginResponse.getUserId(),
            "Login response ID should match registered user ID");
    }

    @Test
    void testRegisterDuplicateEmailThrowsException() {
        authService.register(validRegisterRequest);

        RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> authService.register(validRegisterRequest),
            "Expected RuntimeException due to duplicate email"
        );

        assertTrue(thrown.getMessage().contains("Email is already in use."));
    }

    @Test
    void testLoginWithInvalidPasswordThrowsException() {
        authService.register(validRegisterRequest);

        validLoginRequest.setPassword("wrongpassword");

        assertThrows(
            org.springframework.security.authentication.BadCredentialsException.class,
            () -> authService.login(validLoginRequest),
            "Expected BadCredentialsException for invalid password"
        );
    }
}