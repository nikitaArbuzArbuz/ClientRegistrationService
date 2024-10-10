package ru.t1.java.clientregistrationservice.service;

import java.util.List;

import ru.t1.java.clientregistrationservice.model.Transaction;
import ru.t1.java.clientregistrationservice.model.dto.TransactionDto;

public interface TransactionService {
    void recordTransaction(List<TransactionDto> transactionDto);
    void cancelTransaction(Long transactionId);
}
