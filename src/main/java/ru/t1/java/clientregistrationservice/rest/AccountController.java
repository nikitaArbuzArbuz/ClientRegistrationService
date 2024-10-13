package ru.t1.java.clientregistrationservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.clientregistrationservice.app.domain.dto.AccountDto;
import ru.t1.java.clientregistrationservice.app.domain.dto.MessageResponse;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.service.AccountService;

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

    @PostMapping("/unblock/{transactionId}")
    public ResponseEntity<TransactionDto> unblockAccount(@PathVariable Long transactionId) {
        TransactionDto transactionDto = accountService.unblockAccount(transactionId);
        return ResponseEntity.ok()
                        .header("Server",
                                new MessageResponse("Account unblocked!").getMessage())
                        .body(transactionDto);
    }

    @PostMapping("/blockDebit/{accountId}")
    public ResponseEntity<?> blockDebit(@PathVariable Long accountId) {
        accountService.blockAccount(accountId);
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Account blocked!").getMessage())
                .build();
    }
}
