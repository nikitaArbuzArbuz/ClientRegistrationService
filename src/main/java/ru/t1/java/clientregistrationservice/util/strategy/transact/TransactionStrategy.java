package ru.t1.java.clientregistrationservice.util.strategy.transact;

import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

public interface TransactionStrategy {
    void changeBalance(Account account, Transaction transaction);
}
