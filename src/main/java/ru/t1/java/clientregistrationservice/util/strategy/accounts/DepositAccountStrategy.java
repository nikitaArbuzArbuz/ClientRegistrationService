package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;
import ru.t1.java.clientregistrationservice.app.service.ClientService;
import ru.t1.java.clientregistrationservice.util.strategy.transact.TransactionStrategyFactory;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositAccountStrategy implements AccountStrategy {

    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private final TransactionStrategyFactory transactionStrategyFactory;

    @Transactional
    @Override
    public Account create(Account account) {
        Client client = clientService.getAuthenticatedUser();

        if (client.getAccounts().stream()
                .anyMatch(acc -> acc.getAccountType() == Account.AccountType.DEPOSIT)) {
            throw new RuntimeException("Account deposit already exists");
        }

        log.info("Creating deposit account ID: {}", account.getId());
        return accountRepository.saveAndFlush(account);
    }

    @Override
    public void changeBalance(Account account, Transaction transaction) {
        transactionStrategyFactory.getStrategy(transaction.getType()).changeBalance(account, transaction);
        log.info("Changing deposit account balance ID: {}", account.getId());
    }

    @Override
    public void unblockAccount(Account account, Transaction transaction) {
        account.unblockAccount();
        log.info("Unblocking deposit account ID: {}", account.getId());
        accountRepository.saveAndFlush(account);
    }
}
