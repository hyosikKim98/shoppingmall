package project.shopping.domain.user.model;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String passwordHash;
    private Role role;
    private OffsetDateTime createdAt;

    public static User createNew(String email, String passwordHash, Role role) {
        return new User(null, email, passwordHash, role, OffsetDateTime.now());
    }

    public void setId(Long id) { this.id = id; }
}