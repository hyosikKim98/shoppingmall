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
import project.shopping.domain.user.dto.SignupRequest;
import project.shopping.domain.user.dto.TokenResponse;
import project.shopping.domain.user.model.Role;
import project.shopping.domain.user.model.User;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
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

        String token = jwtTokenProvider.createAccessToken(new AuthPrincipal(saved.getId(), role.asAuthority()));
        return TokenResponse.bearer(token);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtTokenProvider.createAccessToken(new AuthPrincipal(user.getId(), user.getRole().asAuthority()));
        return TokenResponse.bearer(token);
    }

    /** MyBatis 구현은 infrastructure에서 만들 예정. */
    public interface UserRepository {
        User save(User user);
        boolean existsByEmail(String email);
        java.util.Optional<User> findByEmail(String email);
    }
}
