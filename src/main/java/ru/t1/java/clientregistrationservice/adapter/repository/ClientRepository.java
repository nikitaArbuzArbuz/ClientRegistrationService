package ru.t1.java.clientregistrationservice.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByLogin(String username);

    Boolean existsByLogin(String username);

    Boolean existsByEmail(String email);
}
