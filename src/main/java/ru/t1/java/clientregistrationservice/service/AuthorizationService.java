package ru.t1.java.clientregistrationservice.service;

import ru.t1.java.clientregistrationservice.model.dto.ClientDto;
import ru.t1.java.clientregistrationservice.model.dto.JwtResponse;
import ru.t1.java.clientregistrationservice.model.dto.LoginRequest;
import ru.t1.java.clientregistrationservice.model.dto.SignupRequest;

public interface AuthorizationService {
    JwtResponse authorize(LoginRequest loginRequest);

    ClientDto register(SignupRequest signupRequest);
}
