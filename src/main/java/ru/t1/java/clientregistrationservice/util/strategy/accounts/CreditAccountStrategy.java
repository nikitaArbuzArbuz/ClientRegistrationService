package ru.t1.java.clientregistrationservice.util.strategy.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Client;
import ru.t1.java.clientregistrationservice.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.service.ClientService;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditAccountStrategy implements AccountStrategy {

    private final AccountRepository accountRepository;
    private final ClientService clientService;
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
}
