package ru.t1.java.clientregistrationservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.t1.java.clientregistrationservice.adapter.repository.ClientRepository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.service.impl.ClientServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTests {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setLogin("testUser");
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("testUser");
    }

    @Test
    void getAuthenticatedUserShouldReturnClientWhenUserIsAuthenticated() {
        when(clientRepository.findByLogin(client.getLogin())).thenReturn(Optional.of(client));

        Client result = clientService.getAuthenticatedUser();

        assertNotNull(result);
        assertEquals(client.getLogin(), result.getLogin());
    }

    @Test
    void getAuthenticatedUserShouldThrowExceptionWhenUserNotFound() {
        when(clientRepository.findByLogin("testUser")).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            clientService.getAuthenticatedUser();
        });

        assertEquals("User not found!", thrown.getMessage());
    }
}
