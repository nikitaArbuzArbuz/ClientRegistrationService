package ru.t1.java.clientregistrationservice.util.strategy.roles;

import org.springframework.stereotype.Service;
import ru.t1.java.clientregistrationservice.app.domain.entity.Role;
import ru.t1.java.clientregistrationservice.app.domain.entity.RoleEnum;
import ru.t1.java.clientregistrationservice.adapter.repository.RoleRepository;

@Service
public class ModeratorRoleStrategy implements RoleStrategy {

    private final RoleRepository roleRepository;

    public ModeratorRoleStrategy(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRole() {
        return roleRepository.findByName(RoleEnum.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }
}
