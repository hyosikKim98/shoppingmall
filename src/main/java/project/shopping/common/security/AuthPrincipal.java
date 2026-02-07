package project.shopping.common.security;

public record AuthPrincipal(
        Long userId,
        String role
) {}