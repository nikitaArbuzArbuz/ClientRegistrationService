package ru.t1.java.clientregistrationservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.dto.AccountDto;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;
import ru.t1.java.clientregistrationservice.app.mapper.AccountMapper;
import ru.t1.java.clientregistrationservice.app.mapper.TransactionMapper;
import ru.t1.java.clientregistrationservice.app.service.ClientService;
import ru.t1.java.clientregistrationservice.app.service.impl.AccountServiceImpl;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategy;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategyFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {
    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private ClientService clientService;

    @Mock
    private AccountStrategyFactory accountStrategyFactory;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountDto accountDto;
    private Account account;
    private Client client;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setLogin("testUser");

        accountDto = new AccountDto();
        accountDto.setAccountType(Account.AccountType.CREDIT); // Или другой тип

        account = new Account();
        account.setId(1L);
        account.setAccountType(Account.AccountType.CREDIT);
        account.setBlocked(false);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAccount(account);
    }

    @Test
    void createAccountShouldCreateAccountSuccessfully() {
        when(clientService.getAuthenticatedUser()).thenReturn(client);
        when(accountMapper.map(any(), eq(client))).thenReturn(account);
        AccountStrategy accountStrategyMock = mock(AccountStrategy.class);
        when(accountStrategyFactory.getStrategy(account.getAccountType())).thenReturn(accountStrategyMock);
        when(accountStrategyMock.create(account)).thenReturn(account);

        Account createdAccount = accountService.createAccount(accountDto);

        verify(accountMapper).map(accountDto, client);
        verify(accountStrategyFactory).getStrategy(account.getAccountType());
        verify(accountStrategyMock).create(account);
        assertEquals(createdAccount, account);
    }

    @Test
    void unblockAccountShouldUnblockAccountSuccessfully() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));
        AccountStrategy accountStrategyMock = mock(AccountStrategy.class);
        when(accountStrategyFactory.getStrategy(account.getAccountType())).thenReturn(accountStrategyMock);
        when(transactionMapper.map(transaction)).thenReturn(new TransactionDto());

        TransactionDto result = accountService.unblockAccount(1L);

        verify(transactionRepository).findById(1L);
        verify(accountStrategyFactory).getStrategy(account.getAccountType());
        verify(accountStrategyMock).unblockAccount(account, transaction);
        verify(transactionMapper).map(transaction);
        assertNotNull(result);
    }

    @Test
    void unblockAccountShouldThrowExceptionWhenTransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                accountService.unblockAccount(1L));

        assertEquals("Account not found", thrown.getMessage());
    }

    @Test
    void blockAccountShouldThrowExceptionWhenAccountNotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                accountService.blockAccount(1L));

        assertEquals("Account not found", thrown.getMessage());
    }

    @Test
    void existBlockedAccountByTransactionIdShouldReturnTrueIfAccountIsBlocked() {
        account.setBlocked(true);
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));

        boolean result = accountService.existBlockedAccountByTransactionId(1L);

        verify(transactionRepository).findById(1L);
        assertTrue(result);
    }

    @Test
    void existBlockedAccountByTransactionIdShouldThrowExceptionWhenTransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                accountService.existBlockedAccountByTransactionId(1L));

        assertEquals("Transaction not found!", thrown.getMessage());
    }
}
