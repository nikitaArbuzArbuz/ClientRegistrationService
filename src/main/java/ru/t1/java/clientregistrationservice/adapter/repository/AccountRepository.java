package ru.t1.java.clientregistrationservice.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}