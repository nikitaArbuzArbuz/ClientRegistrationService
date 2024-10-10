package ru.t1.java.clientregistrationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.mapper.AccountMapper;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Client;
import ru.t1.java.clientregistrationservice.model.dto.AccountDto;
import ru.t1.java.clientregistrationservice.service.AccountService;
import ru.t1.java.clientregistrationservice.service.ClientService;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategyFactory;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final ClientService clientService;
    private final AccountStrategyFactory accountStrategyFactory;

    @Override
    public Account createAccount(AccountDto accountDto) {
        Client client = clientService.getAuthenticatedUser();
        Account account = accountMapper.map(accountDto, client);

        log.info("Создание счёта для клиента с login = {}", client.getLogin());

        return accountStrategyFactory.getStrategy(account.getAccountType()).create(account);
    }
}
