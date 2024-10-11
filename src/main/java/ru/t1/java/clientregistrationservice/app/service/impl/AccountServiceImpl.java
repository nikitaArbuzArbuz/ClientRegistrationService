package ru.t1.java.clientregistrationservice.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.mapper.AccountMapper;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.domain.dto.AccountDto;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.app.service.AccountService;
import ru.t1.java.clientregistrationservice.app.service.ClientService;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategyFactory;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final ClientService clientService;
    private final AccountStrategyFactory accountStrategyFactory;
    private final TransactionRepository transactionRepository;

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
    public boolean unblockAccount(Long transactionId) {
        try {
            Account account = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Account not found")).getAccount();

            return accountStrategyFactory.getStrategy(account.getAccountType()).unblockAccount(account);
        } catch (OptimisticLockingFailureException e) {
            log.error("Ошибка оптимистической блокировки для transactionId: {}", transactionId, e);
            throw new RuntimeException("Регистрация не удалась, попробуйте еще раз", e);
        }
    }
}
