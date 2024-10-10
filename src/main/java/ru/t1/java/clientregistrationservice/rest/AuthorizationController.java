package ru.t1.java.clientregistrationservice.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.clientregistrationservice.app.domain.dto.*;
import ru.t1.java.clientregistrationservice.app.service.AuthorizationService;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authorizationService.authorize(loginRequest);
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Login succeeded").getMessage())
                .body(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        ClientDto client = authorizationService.register(signUpRequest);
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("User registered successfully!").getMessage())
                .body(client);
    }
}
