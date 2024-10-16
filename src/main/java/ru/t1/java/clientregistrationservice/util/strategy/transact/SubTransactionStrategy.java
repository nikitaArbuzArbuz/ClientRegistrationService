package ru.t1.java.clientregistrationservice.util.strategy.transact;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SubTransactionStrategy implements TransactionStrategy {
    private final TransactionRepository transactionRepository;

    @Override
    public void changeBalance(Account account, Transaction transaction) {
        if (account.getBalance().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            transaction.cancelTransaction();
            transactionRepository.saveAndFlush(transaction);

            throw new RuntimeException("Account does not have enough money");
        }
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
    }
}
