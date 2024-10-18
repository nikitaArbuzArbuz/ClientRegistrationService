package ru.t1.java.clientregistrationservice.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.dto.AccountDto;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;
import ru.t1.java.clientregistrationservice.app.mapper.AccountMapper;
import ru.t1.java.clientregistrationservice.app.mapper.TransactionMapper;
import ru.t1.java.clientregistrationservice.app.service.AccountService;
import ru.t1.java.clientregistrationservice.app.service.ClientService;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategyFactory;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    private final ClientService clientService;
    private final AccountStrategyFactory accountStrategyFactory;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(AccountDto accountDto) {
        Client client = clientService.getAuthenticatedUser();
        Account account = accountMapper.map(accountDto, client);

        log.info("Создание счёта {}", account);
        log.info("Создание счёта для клиента с login = {}", client.getLogin());

        return accountStrategyFactory.getStrategy(account.getAccountType()).create(account);
    }

    @Override
    @Transactional
    public TransactionDto unblockAccount(Long transactionId) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            Account account = transaction.getAccount();

            accountStrategyFactory.getStrategy(account.getAccountType()).unblockAccount(account, transaction);
            return transactionMapper.map(transaction);

        } catch (OptimisticLockingFailureException e) {
            log.error("Ошибка оптимистической блокировки для transactionId: {}", transactionId, e);
            throw new RuntimeException("Регистрация не удалась, попробуйте еще раз", e);
        }
    }

    @Override
    @Transactional
    public void blockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new RuntimeException("Account not found"));
        account.blockAccount();
        log.info("Аккаунт заблокирован ID: {}", accountId);
        accountRepository.save(account);
    }

    @Override
    public boolean existBlockedAccountByTransactionId(Long transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(() ->
                new RuntimeException("Transaction not found!")).getAccount().isBlocked();
    }
}
