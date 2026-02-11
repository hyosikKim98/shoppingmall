package project.shopping.infrastructure.persistence.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import project.shopping.domain.user.model.User;

import java.time.OffsetDateTime;
import java.util.Optional;

@Mapper
public interface UserMapper {
    int insert(User user);
    Optional<User> findByEmail(@Param("email") String email);
    Optional<User> findById(@Param("userId") Long userId);
    boolean existsByEmail(@Param("email") String email);

    int updateRefreshTokenByUserId(
            @Param("userId") Long userId,
            @Param("token") String token,
            @Param("expiresAt") OffsetDateTime expiresAt
    );

    int insertRefreshToken(
            @Param("userId") Long userId,
            @Param("token") String token,
            @Param("expiresAt") OffsetDateTime expiresAt,
            @Param("createdAt") OffsetDateTime createdAt
    );

    int rotateRefreshToken(
            @Param("userId") Long userId,
            @Param("oldToken") String oldToken,
            @Param("newToken") String newToken,
            @Param("newExpiresAt") OffsetDateTime newExpiresAt,
            @Param("now") OffsetDateTime now
    );
}
