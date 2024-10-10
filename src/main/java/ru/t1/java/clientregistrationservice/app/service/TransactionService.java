package ru.t1.java.clientregistrationservice.app.service;

import java.util.List;

import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;

public interface TransactionService {
    void recordTransaction(List<TransactionDto> transactionDto);

    void cancelTransaction(Long transactionId);
}
