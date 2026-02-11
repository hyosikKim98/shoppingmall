package project.shopping.domain.user.port.out;

import project.shopping.domain.user.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long userId);
}