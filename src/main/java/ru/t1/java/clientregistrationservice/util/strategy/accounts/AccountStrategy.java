package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

public interface AccountStrategy {
    Account create(Account account);

    void changeBalance(Account account, Transaction transaction);

    void unblockAccount(Account account, Transaction transaction);
}
