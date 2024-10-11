package ru.t1.java.clientregistrationservice.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.adapter.kafka.KafkaTransactProducer;
import ru.t1.java.clientregistrationservice.adapter.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.adapter.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;
import ru.t1.java.clientregistrationservice.app.mapper.TransactionMapper;
import ru.t1.java.clientregistrationservice.app.service.TransactionService;
import ru.t1.java.clientregistrationservice.util.exceptions.AccountBlockedException;
import ru.t1.java.clientregistrationservice.util.strategy.accounts.AccountStrategyFactory;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTransactProducer kafkaTransactProducer;
    private final AccountStrategyFactory accountStrategyFactory;
    private final TransactionMapper transactionMapper;

    @Transactional
    @Override
    public void recordTransaction(List<TransactionDto> transactionDtoList) {
        transactionDtoList.forEach(transactionDto -> {
            try {
                Account account = accountRepository.findById(transactionDto.getAccountId())
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                Transaction transaction = transactionMapper.map(transactionDto);
                transaction.setAccount(account);

                transactionRepository.save(transaction);
                if (account.isBlocked()) {
                    throw new AccountBlockedException("Account with ID " + account.getId() + " is blocked", transaction.getId());
                }

                if (!transaction.getType().equals(Transaction.TransactionType.CANCEL)) {
                    accountStrategyFactory.getStrategy(account.getAccountType())
                            .changeBalance(account, transaction);
                    log.info("Транзакция с id {} прошла", transaction.getId());

                    transactionRepository.saveAndFlush(transaction);
                } else {
                    cancelTransaction(transaction.getId());
                }

            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic locking failure for transactionDto: {}", transactionDto, e);
            } catch (AccountBlockedException e) {
                log.error("Account is blocked for transactionDto: {} ID", e.getAccountId(), e);
                sendTransactionError(e.getAccountId());
            } catch (Exception e) {
                log.error("Failed to process transactionDto: {}", transactionDto, e);
            }
        });

    }

    @Transactional
    @Override
    public void cancelTransaction(Long transactionId) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            Account account = transaction.getAccount();
            accountStrategyFactory.getStrategy(account.getAccountType()).changeBalance(account, transaction);

            transactionRepository.delete(transaction);
        } catch (OptimisticLockingFailureException e) {
            log.error("Optimistic locking failure for transaction ID: {}", transactionId, e);
        }
    }

    private void sendTransactionError(Long transactionId) {
        kafkaTransactProducer.send("t1_demo_client_transaction_errors", transactionId);
    }
}
