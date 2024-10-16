package ru.t1.java.clientregistrationservice.util.strategy.roles;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RoleStrategyFactory {
    private final Map<String, RoleStrategy> strategies;

    public RoleStrategyFactory(AdminRoleStrategy adminRoleStrategy,
                               UserRoleStrategy userRoleStrategy,
                               ModeratorRoleStrategy moderatorRoleStrategy) {
        strategies = new HashMap<>();
        strategies.put("admin", adminRoleStrategy);
        strategies.put("mod", moderatorRoleStrategy);
        strategies.put("user", userRoleStrategy);
    }

    public RoleStrategy getStrategy(String role) {
        return strategies.getOrDefault(role, strategies.get("user"));
    }
}
