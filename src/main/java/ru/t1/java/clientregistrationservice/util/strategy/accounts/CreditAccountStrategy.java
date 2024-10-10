package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Client;
import ru.t1.java.clientregistrationservice.model.Transaction;
import ru.t1.java.clientregistrationservice.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.service.ClientService;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditAccountStrategy implements AccountStrategy {

    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private final TransactionRepository transactionRepository;
    @Value("${account.limit-for-credit}")
    private double limitCredit;

    @Transactional
    @Override
    public Account create(Account account) {
        Client client = clientService.getAuthenticatedUser();

        if (client.getAccounts().stream()
                .anyMatch(acc -> acc.getAccountType() == Account.AccountType.CREDIT)) {
            throw new RuntimeException("Account credit already exists");
        }

        account.setBalance(new BigDecimal(limitCredit));

        return accountRepository.saveAndFlush(account);
    }

    @Override
    public void changeBalance(Account account, Transaction transaction) {
        if (account.getBalance().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            transaction.cancelTransaction();
            transactionRepository.saveAndFlush(transaction);

            throw new RuntimeException("Account credit does not have enough money");
        }
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        account.checkAndBlockCreditAccount(new BigDecimal(limitCredit));
    }

    @Override
    public boolean unblockAccount(Account account) {
        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            account.unblockAccount();
            accountRepository.saveAndFlush(account);
            retryFailedTransaction(account);
            return true;
        }

        return false;
    }

    @Override
    public void adjustmentBalance(Account account, Transaction transaction) {
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        accountRepository.saveAndFlush(account);
    }

    private void retryFailedTransaction(Account account) {
        List<Transaction> failedTransactions = transactionRepository.findByAccountIdAndIsCancelIsTrue(account.getId());
        for (Transaction transaction : failedTransactions) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            transactionRepository.saveAndFlush(transaction);
        }
    }
}
