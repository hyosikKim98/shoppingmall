package project.shopping.domain.user.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
public class User {
    private Long id;
    private String email;
    private String passwordHash;
    private Role role;
    private OffsetDateTime createdAt;

    public User(Long id, String email, String passwordHash, Role role, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static User createNew(String email, String passwordHash, Role role) {
        return new User(null, email, passwordHash, role, OffsetDateTime.now());
    }

    public void setId(Long id) { this.id = id; }
}