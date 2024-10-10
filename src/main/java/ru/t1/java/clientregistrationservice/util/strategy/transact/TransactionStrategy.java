package ru.t1.java.clientregistrationservice.util.strategy.transact;

import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Transaction;

public interface TransactionStrategy {
    void changeBalance(Account account, Transaction transaction);
}
