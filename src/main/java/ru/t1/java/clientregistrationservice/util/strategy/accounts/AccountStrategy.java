package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Transaction;

public interface AccountStrategy {
    Account create(Account account);

    void changeBalance(Account account, Transaction transaction);

    boolean unblockAccount(Account account);

    void adjustmentBalance(Account account, Transaction transaction);
}
