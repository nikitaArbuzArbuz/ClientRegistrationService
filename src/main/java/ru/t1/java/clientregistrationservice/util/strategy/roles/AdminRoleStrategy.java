package ru.t1.java.clientregistrationservice.util.strategy.roles;

import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.adapter.repository.RoleRepository;
import ru.t1.java.clientregistrationservice.app.domain.entity.Role;
import ru.t1.java.clientregistrationservice.app.domain.entity.RoleEnum;

@Service
public class AdminRoleStrategy implements RoleStrategy {

    private final RoleRepository roleRepository;

    public AdminRoleStrategy(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRole() {
        return roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }
}
