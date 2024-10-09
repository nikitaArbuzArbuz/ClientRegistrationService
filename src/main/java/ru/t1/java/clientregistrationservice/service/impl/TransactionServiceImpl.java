package ru.t1.java.clientregistrationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.clientregistrationservice.mapper.TransactionMapper;
import ru.t1.java.clientregistrationservice.model.Account;
import ru.t1.java.clientregistrationservice.model.Transaction;
import ru.t1.java.clientregistrationservice.model.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.repository.AccountRepository;
import ru.t1.java.clientregistrationservice.repository.TransactionRepository;
import ru.t1.java.clientregistrationservice.service.TransactionService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    @Value("${account.limit-for-credit}")
    private double limitCredit;

    @Transactional
    @Override
    public Transaction recordTransaction(TransactionDto transactionDto) {
        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = transactionMapper.map(transactionDto);
        transaction.setAccount(account);

        // логика перевода например

        transaction.getAccount().checkAndBlockCreditAccount(new BigDecimal(limitCredit));
        log.info("Транзакция с id {} прошла", transaction.getId());

        return transactionRepository.saveAndFlush(transaction);
    }
}
