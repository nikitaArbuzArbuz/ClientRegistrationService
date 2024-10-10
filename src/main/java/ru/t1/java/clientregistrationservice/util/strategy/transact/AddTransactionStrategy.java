package ru.t1.java.clientregistrationservice.util.strategy.transact;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

@Service
@RequiredArgsConstructor
public class AddTransactionStrategy implements TransactionStrategy {

    @Override
    public void changeBalance(Account account, Transaction transaction) {
        account.setBalance(account.getBalance().add(transaction.getAmount()));
    }
}
