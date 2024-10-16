package ru.t1.java.clientregistrationservice.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Role;
import ru.t1.java.clientregistrationservice.app.domain.entity.RoleEnum;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
}
