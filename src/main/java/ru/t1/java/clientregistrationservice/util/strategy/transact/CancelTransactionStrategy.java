package ru.t1.java.clientregistrationservice.util.strategy.transact;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Transaction;
import ru.t1.java.clientregistrationservice.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class CancelTransactionStrategy implements TransactionStrategy {

    private final TransactionRepository transactionRepository;


    @Override
    @Transactional
    public void changeBalance(Account account, Transaction currentTransaction) {
        Transaction dbTransaction = transactionRepository.findById(currentTransaction.getId()).orElseThrow(() ->
                new RuntimeException("Transaction not found"));

        if (dbTransaction.getType() == Transaction.TransactionType.ADD) {
            account.setBalance(account.getBalance().subtract(currentTransaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(currentTransaction.getAmount()));
        }
    }
}
