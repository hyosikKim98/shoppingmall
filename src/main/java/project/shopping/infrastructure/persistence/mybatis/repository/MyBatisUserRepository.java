package project.shopping.infrastructure.persistence.mybatis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.shopping.domain.user.model.User;
import project.shopping.domain.user.service.AuthService;
import project.shopping.infrastructure.persistence.mybatis.mapper.UserMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisUserRepository implements AuthService.UserRepository {

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
}
