package ru.t1.java.clientregistrationservice.util.strategy.roles;

import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.model.Role;
import ru.t1.java.clientregistrationservice.model.RoleEnum;
import ru.t1.java.clientregistrationservice.repository.RoleRepository;

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
