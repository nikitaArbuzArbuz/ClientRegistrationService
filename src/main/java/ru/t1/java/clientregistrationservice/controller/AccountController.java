package ru.t1.java.clientregistrationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.clientregistrationservice.model.dto.AccountDto;
import ru.t1.java.clientregistrationservice.model.dto.MessageResponse;
import ru.t1.java.clientregistrationservice.service.AccountService;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Account created successfully!").getMessage())
                .body(accountService.createAccount(accountDto));
    }

    @PostMapping("/unblock/{accountId}")
    public ResponseEntity<String> unblockAccount(@PathVariable Long accountId) {
        return accountService.unblockAccount(accountId) ?
                ResponseEntity.ok("Account unblocked and transaction retried") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient funds to unblock credit account");
    }
}
