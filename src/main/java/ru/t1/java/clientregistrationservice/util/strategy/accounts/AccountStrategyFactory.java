package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;

import java.util.EnumMap;
import java.util.Map;

@Service
public class AccountStrategyFactory {
    private final Map<Account.AccountType, AccountStrategy> strategies;

    public AccountStrategyFactory(CreditAccountStrategy creditAccountStrategy,
                                  DepositAccountStrategy depositAccountStrategy) {
        strategies = new EnumMap<>(Account.AccountType.class);
        strategies.put(Account.AccountType.CREDIT, creditAccountStrategy);
        strategies.put(Account.AccountType.DEPOSIT, depositAccountStrategy);
    }

    public AccountStrategy getStrategy(Account.AccountType accountType) {
        return strategies.get(accountType);
    }
}
