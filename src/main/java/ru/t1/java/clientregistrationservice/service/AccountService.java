package ru.t1.java.clientregistrationservice.service;

import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.dto.AccountDto;

public interface AccountService {
    Account createAccount(AccountDto accountDto);

    boolean unblockAccount(Long accountId);
}
