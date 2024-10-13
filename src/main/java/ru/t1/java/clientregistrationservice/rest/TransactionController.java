package ru.t1.java.clientregistrationservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.clientregistrationservice.app.domain.dto.MessageResponse;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.service.TransactionService;

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

    @PostMapping("/delete")
    public ResponseEntity<?> deleteTransaction(@RequestBody Long transactionId) {
        transactionService.deleteTransactionById(transactionId);
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Transaction deleted!").getMessage())
                .build();
    }

    @PostMapping("/newById")
    public ResponseEntity<?> createTransactionById(@RequestBody Long transactionId) {
        transactionService.recordTransaction(transactionId);
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Transaction success!").getMessage())
                .build();
    }

    @GetMapping("/getCanceled")
    public List<TransactionDto> getCanceledTransactions() {
        return ResponseEntity.ok()
                .header("Server",
                        new MessageResponse("Get transactions success!").getMessage())
                .body(transactionService.getCanceledTransactions()).getBody();
    }
}
