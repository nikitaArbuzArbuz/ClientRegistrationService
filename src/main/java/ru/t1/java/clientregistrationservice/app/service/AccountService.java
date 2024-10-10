package ru.t1.java.clientregistrationservice.app.service;

import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.dto.AccountDto;

public interface AccountService {
    Account createAccount(AccountDto accountDto);

    boolean unblockAccount(Long accountId);
}
