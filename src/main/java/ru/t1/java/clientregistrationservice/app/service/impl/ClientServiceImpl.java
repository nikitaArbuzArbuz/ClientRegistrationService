package ru.t1.java.clientregistrationservice.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.adapter.repository.ClientRepository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.service.ClientService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Override
    public Client getAuthenticatedUser() {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        String login = authUser.getName();
        log.info("Получаем объект залогиненного пользователя c login: {}", login);
        return clientRepository.findByLogin(login).orElseThrow(() ->
                new UsernameNotFoundException("Пользователь не найден"));
    }
}
