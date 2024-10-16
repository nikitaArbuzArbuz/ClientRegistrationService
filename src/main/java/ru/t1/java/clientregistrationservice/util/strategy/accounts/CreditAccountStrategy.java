package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;
import ru.t1.java.clientregistrationservice.app.service.ClientService;
import ru.t1.java.clientregistrationservice.util.strategy.transact.TransactionStrategyFactory;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditAccountStrategy implements AccountStrategy {

    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private final TransactionRepository transactionRepository;
    private final TransactionStrategyFactory transactionStrategyFactory;
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
        log.info("Creating credit account ID: {}", account.getId());
        return accountRepository.saveAndFlush(account);
    }

    @Override
    public void changeBalance(Account account, Transaction transaction) {
        transactionStrategyFactory.getStrategy(transaction.getType()).changeBalance(account, transaction);
        account.checkAndBlockCreditAccount(account.getBalance());
        log.info("Changing credit account ID: {}", account.getId());
    }

    @Override
    public void unblockAccount(Account account, Transaction transaction) {
        if (account.getBalance().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) >= 0 ||
                transaction.getType().equals(Transaction.TransactionType.ADD)) {
            account.unblockAccount();
            log.info("Unblocking account ID: {}", account.getId());
            accountRepository.saveAndFlush(account);
            retryFailedTransaction(account);
        }
    }

    private void retryFailedTransaction(Account account) {
        List<Transaction> failedTransactions = transactionRepository.findByAccountIdAndIsCancelIsTrue(account.getId());
        for (Transaction transaction : failedTransactions) {
            changeBalance(account, transaction);
            transaction.applyTransaction();
            transactionRepository.saveAndFlush(transaction);
            log.info("Транзакция повторно прошла успешно");
        }
    }
}