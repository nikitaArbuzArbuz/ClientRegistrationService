package ru.t1.java.clientregistrationservice.service;

import ru.t1.java.clientregistrationservice.model.Transaction;
import ru.t1.java.clientregistrationservice.model.dto.TransactionDto;

public interface TransactionService {
    Transaction recordTransaction(TransactionDto transactionDto);
}
