package ru.t1.java.clientregistrationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.clientregistrationservice.model.dto.MessageResponse;
import ru.t1.java.clientregistrationservice.model.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.service.TransactionService;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transact")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/new")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDto transactionDto) {
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Transaction success!").getMessage())
                .body(transactionService.recordTransaction(transactionDto));
    }
}
