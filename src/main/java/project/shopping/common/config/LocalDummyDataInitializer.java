package project.shopping.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.shopping.domain.user.model.Role;
import project.shopping.domain.user.model.User;
import project.shopping.infrastructure.persistence.mybatis.mapper.UserMapper;

@Component
@Profile("local")
@ConditionalOnProperty(name = "seed.local-dummy.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class LocalDummyDataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User seller = ensureSeller();
        User customer = ensureUser();
    }

    private User ensureSeller() {
        String sellerEmail = "seller@example.com";
        return userMapper.findByEmail(sellerEmail).orElseGet(() -> {
            String hash = passwordEncoder.encode("1234");
            User newSeller = User.createNew(sellerEmail, hash, Role.SELLER);
            userMapper.insert(newSeller);
            return newSeller;
        });
    }

    private User ensureUser() {
        String userEmail = "user@example.com";
        return userMapper.findByEmail(userEmail).orElseGet(() -> {
            String hash = passwordEncoder.encode("1234");
            User newUser = User.createNew(userEmail, hash, Role.CUSTOMER);
            userMapper.insert(newUser);
            return newUser;
        });
    }

}
