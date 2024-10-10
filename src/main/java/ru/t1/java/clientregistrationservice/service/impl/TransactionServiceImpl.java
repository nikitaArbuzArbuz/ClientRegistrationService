package ru.t1.java.clientregistrationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.kafka.KafkaTransactProducer;
import ru.t1.java.clientregistrationservice.mapper.TransactionMapper;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Transaction;
import ru.t1.java.clientregistrationservice.model.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.service.ClientService;
import ru.t1.java.clientregistrationservice.service.TransactionService;
import ru.t1.java.clientregistrationservice.util.exceptions.AccountBlockedException;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategyFactory;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private final KafkaTransactProducer kafkaTransactProducer;
    private final AccountStrategyFactory accountStrategyFactory;
    private final TransactionMapper transactionMapper;

    @Transactional
    @Override
    public void recordTransaction(List<TransactionDto> transactionDtoList) {
        for (TransactionDto transactionDto : transactionDtoList) {
            try {
                Account account = accountRepository.findById(transactionDto.getAccountId())
                        .orElseThrow(() -> new RuntimeException("Account not found"));
                checkAccountByClient(account);

                Transaction transaction = transactionMapper.map(transactionDto);
                if (account.isBlocked()) {
                    throw new AccountBlockedException("Account with ID " + account.getId() + " is blocked", transaction.getId());
                }

                transaction.setAccount(account);

                accountStrategyFactory.getStrategy(account.getAccountType())
                        .changeBalance(account, transaction);

                log.info("Транзакция с id {} прошла", transaction.getId());

                transactionRepository.saveAndFlush(transaction);

            } catch (AccountBlockedException e) {
                log.error("Account is blocked for transactionDto: {} ID", e.getAccountId(), e);
                sendTransactionError(e.getAccountId());
            } catch (Exception e) {
                log.error("Failed to process transactionDto: {}", transactionDto, e);
            }
        }
    }

    @Transactional
    @Override
    public void cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Account account = transaction.getAccount();
        accountStrategyFactory.getStrategy(account.getAccountType()).adjustmentBalance(account, transaction);

        transactionRepository.delete(transaction);
    }

    private void checkAccountByClient(Account account) {
        if (!account.getClient().getId().equals(clientService.getAuthenticatedUser().getId())) {
            throw new RuntimeException("У клиента нет такого счёта");
        }
    }

    private void sendTransactionError(Long transactionId) {
        kafkaTransactProducer.send("t1_demo_client_transaction_errors", transactionId);
    }
}
