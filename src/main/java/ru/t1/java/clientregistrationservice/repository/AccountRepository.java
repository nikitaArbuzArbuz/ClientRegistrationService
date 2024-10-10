package ru.t1.java.clientregistrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.clientregistrationservice.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}