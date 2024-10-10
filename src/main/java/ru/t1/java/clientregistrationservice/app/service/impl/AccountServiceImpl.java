package ru.t1.java.clientregistrationservice.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(AccountDto accountDto) {
//        Client client = clientService.getAuthenticatedUser();
//        Account account = accountMapper.map(accountDto, client);
//
//        log.info("Create account {}", account);
//        log.info("Создание счёта для клиента с login = {}", client.getLogin());
//
//        return accountStrategyFactory.getStrategy(account.getAccountType()).create(account);
        return null;
    }

    @Override
    @Transactional
    public boolean unblockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return accountStrategyFactory.getStrategy(account.getAccountType()).unblockAccount(account);
    }
}
