package ru.t1.java.clientregistrationservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import ru.t1.java.clientregistrationservice.adapter.kafka.KafkaTransactProducer;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;
import ru.t1.java.clientregistrationservice.app.mapper.TransactionMapper;
import ru.t1.java.clientregistrationservice.app.service.impl.TransactionServiceImpl;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategy;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategyFactory;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private KafkaTransactProducer kafkaTransactProducer;

    @Mock
    private AccountStrategyFactory accountStrategyFactory;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountStrategy accountStrategy;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account account;
    private TransactionDto transactionDto;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setBlocked(false);
        account.setAccountType(Account.AccountType.CREDIT);

        transactionDto = new TransactionDto();
        transactionDto.setAccountId(1L);
        transactionDto.setType(Transaction.TransactionType.ADD);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAccount(account);
        transaction.setType(transactionDto.getType());
    }

    @Test
    void recordTransactionShouldSaveTransactionWhenAccountIsNotBlocked() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionMapper.map(transactionDto)).thenReturn(transaction);

        transactionService.recordTransaction(Collections.singletonList(transactionDto));

        verify(transactionRepository).save(transaction);
        verify(kafkaTransactProducer, never()).sendTransact(any());
    }

    @Test
    void recordTransactionShouldCancelTransactionWhenAccountIsBlocked() {
        account.setBlocked(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionMapper.map(transactionDto)).thenReturn(transaction);

        transactionService.recordTransaction(Collections.singletonList(transactionDto));

        verify(transactionRepository).save(transaction);
        verify(kafkaTransactProducer).sendTransact(transaction.getId());
    }

    @Test
    void cancelTransactionShouldDeleteTransaction() {
        when(accountStrategyFactory.getStrategy(account.getAccountType())).thenReturn(accountStrategy);
        when(transactionRepository.findLastByAccountId(account.getId())).thenReturn(Optional.of(transaction));

        transactionService.cancelTransaction(transaction);

        verify(transactionRepository).delete(transaction);
        verify(accountStrategy).changeBalance(account, transaction);
    }

    @Test
    void cancelTransactionShouldThrowExceptionWhenTransactionNotFound() {
        when(transactionRepository.findLastByAccountId(account.getId())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                transactionService.cancelTransaction(transaction));

        assertEquals("Transaction not found", thrown.getMessage());
    }

    @Test
    void recordTransactionShouldHandleOptimisticLockingException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionMapper.map(transactionDto)).thenReturn(transaction);

        doThrow(OptimisticLockingFailureException.class).when(transactionRepository).save(any());

        transactionService.recordTransaction(Collections.singletonList(transactionDto));
    }
}

