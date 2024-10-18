package ru.t1.java.clientregistrationservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.t1.java.clientregistrationservice.adapter.repository.ClientRepository;
import ru.t1.java.clientregistrationservice.app.domain.dto.ClientDto;
import ru.t1.java.clientregistrationservice.app.domain.dto.JwtResponse;
import ru.t1.java.clientregistrationservice.app.domain.dto.LoginRequest;
import ru.t1.java.clientregistrationservice.app.domain.dto.SignupRequest;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.mapper.ClientMapper;
import ru.t1.java.clientregistrationservice.app.service.impl.AuthorizationServiceImpl;
import ru.t1.java.clientregistrationservice.app.service.impl.UserDetailsImpl;
import ru.t1.java.clientregistrationservice.util.JwtUtils;
import ru.t1.java.clientregistrationservice.util.strategy.roles.RoleStrategy;
import ru.t1.java.clientregistrationservice.util.strategy.roles.RoleStrategyFactory;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTests {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RoleStrategyFactory roleStrategyFactory;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    private LoginRequest loginRequest;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testUser", "testPassword");
        userDetails = new UserDetailsImpl(1L, "testUser", "testEmail", "testPassword", Collections.emptyList());
    }

    @Test
    void authorizeShouldReturnJwtResponseWhenAuthenticationIsSuccessful() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("jwt.token.here");

        JwtResponse response = authorizationService.authorize(loginRequest);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.getToken());
        assertEquals("testUser", response.getUsername());
    }

    @Test
    void registerShouldRegisterUserSuccessfully() {
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername("newUser");
        signUpRequest.setEmail("newEmail@test.com");
        signUpRequest.setPassword("password");
        signUpRequest.setRole(Collections.singleton("user"));

        when(clientRepository.existsByLogin(signUpRequest.getUsername())).thenReturn(false);
        when(clientRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(roleStrategyFactory.getStrategy("user")).thenReturn(mock(RoleStrategy.class));
        when(clientMapper.map(any(Client.class))).thenReturn(new ClientDto());

        ClientDto clientDto = authorizationService.register(signUpRequest);

        verify(clientRepository).saveAndFlush(any(Client.class));
        assertNotNull(clientDto);
    }

    @Test
    void registerShouldThrowExceptionWhenUsernameIsTaken() {
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername("existingUser");
        when(clientRepository.existsByLogin(signUpRequest.getUsername())).thenReturn(true);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authorizationService.register(signUpRequest);
        });

        assertEquals("Error: Username is already taken!", thrown.getMessage());
    }

    @Test
    void registerShouldThrowExceptionWhenEmailIsTaken() {
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail("existingEmail@test.com");
        when(clientRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authorizationService.register(signUpRequest);
        });

        assertEquals("Error: Email is already in use!", thrown.getMessage());
    }
}
