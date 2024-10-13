package ru.t1.java.clientregistrationservice.app.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.app.mapper.ClientMapper;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.domain.entity.Role;
import ru.t1.java.clientregistrationservice.app.domain.dto.ClientDto;
import ru.t1.java.clientregistrationservice.app.domain.dto.JwtResponse;
import ru.t1.java.clientregistrationservice.app.domain.dto.LoginRequest;
import ru.t1.java.clientregistrationservice.app.domain.dto.SignupRequest;
import ru.t1.java.clientregistrationservice.adapter.repository.ClientRepository;
import ru.t1.java.clientregistrationservice.app.service.AuthorizationService;
import ru.t1.java.clientregistrationservice.util.JwtUtils;
import ru.t1.java.clientregistrationservice.util.strategy.roles.RoleStrategyFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ClientRepository clientRepository;
    private final RoleStrategyFactory roleStrategyFactory;
    private final PasswordEncoder encoder;
    private final ClientMapper clientMapper;

    @Override
    public JwtResponse authorize(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("Пользователь с Login {} успешно аутентифицирован", userDetails.getUsername());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    @Transactional
    @Override
    public ClientDto register(SignupRequest signUpRequest) {

        if (clientRepository.existsByLogin(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (clientRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        Client client = new Client(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles.isEmpty()) {
            roles.add(roleStrategyFactory.getStrategy("user").getRole());
        } else {
            strRoles.forEach(role ->
                    roles.add(roleStrategyFactory.getStrategy(role).getRole())
            );
        }

        client.setRoles(roles);
        clientRepository.saveAndFlush(client);

        log.info("Пользователь успешно зарегистрирован login {}", client.getLogin());

        return clientMapper.map(client);
    }
}
