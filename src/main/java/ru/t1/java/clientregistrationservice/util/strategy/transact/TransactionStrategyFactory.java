package ru.t1.java.clientregistrationservice.util.strategy.transact;

import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

import java.util.EnumMap;
import java.util.Map;

@Service
public class TransactionStrategyFactory {
    private final Map<Transaction.TransactionType, TransactionStrategy> strategies;

    public TransactionStrategyFactory(AddTransactionStrategy addTransactionStrategy,
                                      SubTransactionStrategy subTransactionStrategy,
                                      CancelTransactionStrategy cancelTransactionStrategy) {
        strategies = new EnumMap<>(Transaction.TransactionType.class);
        strategies.put(Transaction.TransactionType.ADD, addTransactionStrategy);
        strategies.put(Transaction.TransactionType.CANCEL, cancelTransactionStrategy);
        strategies.put(Transaction.TransactionType.SUBTRACT, subTransactionStrategy);
    }

    public TransactionStrategy getStrategy(Transaction.TransactionType transactionType) {
        return strategies.get(transactionType);
    }
}
