package ru.t1.java.clientregistrationservice.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}