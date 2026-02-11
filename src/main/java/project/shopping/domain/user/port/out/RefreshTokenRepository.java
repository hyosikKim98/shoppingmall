package project.shopping.domain.user.port.out;

import java.time.OffsetDateTime;

public interface RefreshTokenRepository {
    void saveOrReplace(Long userId, String refreshToken, OffsetDateTime expiresAt);
    boolean rotate(Long userId, String oldRefreshToken, String newRefreshToken, OffsetDateTime newExpiresAt, OffsetDateTime now);
}
