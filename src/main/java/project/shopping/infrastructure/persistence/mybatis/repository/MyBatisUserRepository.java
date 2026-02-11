package project.shopping.infrastructure.persistence.mybatis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.shopping.domain.user.model.User;
import project.shopping.domain.user.port.out.RefreshTokenRepository;
import project.shopping.domain.user.port.out.UserRepository;
import project.shopping.infrastructure.persistence.mybatis.mapper.UserMapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisUserRepository implements UserRepository, RefreshTokenRepository {

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

    @Override
    public Optional<User> findById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public void saveOrReplace(Long userId, String refreshToken, OffsetDateTime expiresAt) {
        int updated = userMapper.updateRefreshTokenByUserId(userId, refreshToken, expiresAt);
        if (updated == 0) {
            userMapper.insertRefreshToken(userId, refreshToken, expiresAt, OffsetDateTime.now(ZoneOffset.UTC));
        }
    }

    @Override
    public boolean rotate(Long userId, String oldRefreshToken, String newRefreshToken, OffsetDateTime newExpiresAt, OffsetDateTime now) {
        return userMapper.rotateRefreshToken(userId, oldRefreshToken, newRefreshToken, newExpiresAt, now) > 0;
    }
}
