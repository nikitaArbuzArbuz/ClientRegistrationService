package ru.t1.java.clientregistrationservice.util.strategy.transact;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

@Service
@RequiredArgsConstructor
public class CancelTransactionStrategy implements TransactionStrategy {

    private final TransactionRepository transactionRepository;


    @Override
    public void changeBalance(Account account, Transaction currentTransaction) {
        Transaction dbTransaction = transactionRepository.findLastByAccountId(account.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (dbTransaction.getType() == Transaction.TransactionType.ADD) {
            account.setBalance(account.getBalance().subtract(dbTransaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(dbTransaction.getAmount()));
        }
    }
}
