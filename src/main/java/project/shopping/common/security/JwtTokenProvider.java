package project.shopping.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.shopping.common.exception.BusinessException;
import project.shopping.common.exception.ErrorCode;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessTokenExpMs;
    private final long refreshTokenExpMs;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access.expiration}") long accessTokenExpMs,
            @Value("${jwt.refresh.expiration}") long refreshTokenExpMs,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpMs = accessTokenExpMs;
        this.refreshTokenExpMs = refreshTokenExpMs;
        this.issuer = issuer;
    }

    public String createAccessToken(AuthPrincipal principal) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(accessTokenExpMs);

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .subject(String.valueOf(principal.userId()))
                .claim(CLAIM_ROLE, principal.role())
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(refreshTokenExpMs);

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .subject(String.valueOf(userId))
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
                .signWith(secretKey)
                .compact();
    }

    public AuthPrincipal parse(String token) {
        Claims claims = parseClaims(token);
        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
        if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Invalid access token");
        }

        Long userId = Long.valueOf(claims.getSubject());
        String role = claims.get(CLAIM_ROLE, String.class);
        return new AuthPrincipal(userId, role);
    }

    public Long parseRefreshToken(String token) {
        Claims claims = parseClaims(token);
        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
        if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Invalid refresh token");
        }

        return Long.valueOf(claims.getSubject());
    }

    public Instant getExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
    }
}
