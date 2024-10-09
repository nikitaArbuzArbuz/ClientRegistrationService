package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import ru.t1.java.clientregistrationservice.model.Account;

public interface AccountStrategy {
    Account create(Account account);
}
