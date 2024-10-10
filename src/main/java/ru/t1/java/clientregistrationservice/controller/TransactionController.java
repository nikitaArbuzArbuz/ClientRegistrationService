package ru.t1.java.clientregistrationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.clientregistrationservice.model.dto.MessageResponse;
import ru.t1.java.clientregistrationservice.model.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.service.TransactionService;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transact")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/new")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDto transactionDto) {
        transactionService.recordTransaction(List.of(transactionDto));
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Transaction success!").getMessage())
                .build();
    }

    @PostMapping("/cancel/{transactionId}")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long transactionId) {
        transactionService.cancelTransaction(transactionId);
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Transaction cancelled success!").getMessage())
                .build();
    }
}
