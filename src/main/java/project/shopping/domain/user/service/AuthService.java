package project.shopping.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.shopping.common.exception.BusinessException;
import project.shopping.common.exception.ErrorCode;
import project.shopping.common.security.AuthPrincipal;
import project.shopping.common.security.JwtTokenProvider;
import project.shopping.domain.user.dto.LoginRequest;
import project.shopping.domain.user.dto.RefreshTokenRequest;
import project.shopping.domain.user.dto.SignupRequest;
import project.shopping.domain.user.dto.TokenResponse;
import project.shopping.domain.user.model.Role;
import project.shopping.domain.user.model.User;
import project.shopping.domain.user.port.out.RefreshTokenRepository;
import project.shopping.domain.user.port.out.UserRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Email already exists");
        }

        Role role = Role.valueOf(req.role().toUpperCase(Locale.ROOT));
        String hash = passwordEncoder.encode(req.password());

        User saved = userRepository.save(User.createNew(req.email(), hash, role));
        return issueAndStoreTokens(saved);
    }

    @Transactional
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }

        return issueAndStoreTokens(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest req) {
        Long userId = jwtTokenProvider.parseRefreshToken(req.refreshToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid refresh token"));

        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
        OffsetDateTime newRefreshExp = OffsetDateTime.ofInstant(jwtTokenProvider.getExpiration(newRefreshToken), ZoneOffset.UTC);

        boolean rotated = refreshTokenRepository.rotate(userId, req.refreshToken(), newRefreshToken, newRefreshExp, OffsetDateTime.now(ZoneOffset.UTC));
        if (!rotated) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }

        String accessToken = jwtTokenProvider.createAccessToken(new AuthPrincipal(user.getId(), user.getRole().asAuthority()));
        return TokenResponse.bearer(accessToken, newRefreshToken);
    }

    private TokenResponse issueAndStoreTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(new AuthPrincipal(user.getId(), user.getRole().asAuthority()));
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        OffsetDateTime refreshExp = OffsetDateTime.ofInstant(jwtTokenProvider.getExpiration(refreshToken), ZoneOffset.UTC);

        refreshTokenRepository.saveOrReplace(user.getId(), refreshToken, refreshExp);
        return TokenResponse.bearer(accessToken, refreshToken);
    }

}
