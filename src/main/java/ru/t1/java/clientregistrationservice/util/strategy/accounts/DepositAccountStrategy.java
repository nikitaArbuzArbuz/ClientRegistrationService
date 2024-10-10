package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Client;
import ru.t1.java.clientregistrationservice.model.Transaction;
import ru.t1.java.clientregistrationservice.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.service.ClientService;

@Service
@RequiredArgsConstructor
public class DepositAccountStrategy implements AccountStrategy {

    private final AccountRepository accountRepository;
    private final ClientService clientService;

    @Transactional
    @Override
    public Account create(Account account) {
        Client client = clientService.getAuthenticatedUser();

        if (client.getAccounts().stream()
                .anyMatch(acc -> acc.getAccountType() == Account.AccountType.DEPOSIT)) {
            throw new RuntimeException("Account deposit already exists");
        }
        return accountRepository.saveAndFlush(account);
    }

    @Override
    public void changeBalance(Account account, Transaction transaction) {
        account.setBalance(account.getBalance().add(transaction.getAmount()));
    }

    @Override
    public boolean unblockAccount(Account account) {
        account.unblockAccount();
        accountRepository.saveAndFlush(account);
        return true;
    }

    @Override
    public void adjustmentBalance(Account account, Transaction transaction) {
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        accountRepository.saveAndFlush(account);
    }
}
