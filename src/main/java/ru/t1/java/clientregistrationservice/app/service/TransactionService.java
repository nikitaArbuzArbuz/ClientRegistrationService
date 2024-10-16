package ru.t1.java.clientregistrationservice.app.service;

import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

import java.util.List;

public interface TransactionService {
    void recordTransaction(List<TransactionDto> transactionDto);

    void cancelTransaction(Transaction transaction);
}
