package project.shopping.infrastructure.persistence.jpa.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import project.shopping.domain.user.model.User;
import project.shopping.domain.user.service.AuthService;

import java.util.Optional;

@Repository
@Profile("jpa")
public class JpaUserRepository implements AuthService.UserRepository {
    @Override
    public User save(User user) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public boolean existsByEmail(String email) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public Optional<User> findByEmail(String email) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }
}
