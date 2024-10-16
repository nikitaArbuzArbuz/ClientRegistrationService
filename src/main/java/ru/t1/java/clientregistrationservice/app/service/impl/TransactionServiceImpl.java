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

                if (!transaction.getType().equals(Transaction.TransactionType.CANCEL)) {
                    transactionRepository.save(transaction);

                    if (account.isBlocked()) {
                        transaction.cancelTransaction();
                        transactionRepository.saveAndFlush(transaction);
                        throw new AccountBlockedException("Account with ID " + account.getId() + " is blocked", transaction.getId());
                    }

                    accountStrategyFactory.getStrategy(account.getAccountType()).changeBalance(account, transaction);
                    log.info("Транзакция с id {} прошла", transaction.getId());

                    transactionRepository.saveAndFlush(transaction);
                } else {
                    cancelTransaction(transaction);
                }

            } catch (OptimisticLockingFailureException e) {
                log.error("Оптимистичный отказ блокировки для transactionDto: {}", transactionDto, e);
            } catch (AccountBlockedException e) {
                log.error("Аккаунт заблокирован для transactionDto: {} ID", e.getAccountId(), e);
                sendTransactionError(e.getAccountId());
            } catch (Exception e) {
                log.error("Ошибка в обработке transactionDto: {}", transactionDto, e);
            }
        });

    }

    @Transactional
    @Override
    public void cancelTransaction(Transaction transaction) {
        Account account = transaction.getAccount();
        Transaction dbTransaction = transactionRepository.findLastByAccountId(account.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        try {
            accountStrategyFactory.getStrategy(account.getAccountType()).changeBalance(account, transaction);
            transactionRepository.delete(dbTransaction);
            log.info("Транзакция было отменена и удалена ID {} - {}", dbTransaction.getId(), dbTransaction);
        } catch (OptimisticLockingFailureException e) {
            log.error("Ошибка оптимистичной блокировки для транзакции ID: {}", dbTransaction.getId(), e);
        }
    }

    private void sendTransactionError(Long transactionId) {
        kafkaTransactProducer.sendTransact(transactionId);
    }
}
