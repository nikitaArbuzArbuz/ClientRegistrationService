package ru.t1.java.clientregistrationservice.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
import ru.t1.java.clientregistrationservice.app.domain.entity.Transaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdAndIsCancelIsTrue(Long id);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.transactionDate DESC LIMIT 1")
    Optional<Transaction> findLastByAccountId(@Param("accountId") Long accountId);

    List<Transaction> findByIsCancel(boolean isCancel);
}
