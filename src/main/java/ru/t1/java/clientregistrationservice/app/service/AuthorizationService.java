package ru.t1.java.clientregistrationservice.app.service;

import ru.t1.java.clientregistrationservice.app.domain.dto.ClientDto;
import ru.t1.java.clientregistrationservice.app.domain.dto.JwtResponse;
import ru.t1.java.clientregistrationservice.app.domain.dto.LoginRequest;
import ru.t1.java.clientregistrationservice.app.domain.dto.SignupRequest;

public interface AuthorizationService {
    JwtResponse authorize(LoginRequest loginRequest);

    ClientDto register(SignupRequest signupRequest);
}
