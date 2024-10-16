package ru.t1.java.clientregistrationservice.app.service;

import ru.t1.java.clientregistrationservice.app.domain.dto.AccountDto;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;

public interface AccountService {
    Account createAccount(AccountDto accountDto);

    TransactionDto unblockAccount(Long accountId);

    void blockAccount(Long accountId);

    boolean existBlockedAccountByTransactionId(Long transactionId);
}
