package project.shopping.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.shopping.domain.user.model.Role;
import project.shopping.domain.user.model.User;
import project.shopping.infrastructure.persistence.mybatis.mapper.UserMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Profile("local")
@ConditionalOnProperty(name = "seed.bulk.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class ProductBulkDataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        User seller = ensureSeller();
        User customer = ensureUser();

        int total = Integer.parseInt(System.getProperty("seed.products.count", "100000"));
        int batchSize = 5000;

        Long existing = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Long.class);
        if (existing == null) existing = 0L;
        if (existing >= total) return;

        String sql = """
                INSERT INTO products (seller_id, name, price, stock, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        int start = existing.intValue() + 1;

        for (int base = start; base <= total; base += batchSize) {
            final int from = base;
            final int to = Math.min(base + batchSize - 1, total);
            final int size = to - from + 1;

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    int n = from + i;
                    ThreadLocalRandom r = ThreadLocalRandom.current();

                    long sellerId = r.nextLong(1, 1001);
                    String name = (r.nextBoolean() ? "Phone-" : "Laptop-") + n;
                    long price = r.nextLong(1_000, 1_000_001);
                    int stock = r.nextInt(0, 501);
                    String status = r.nextDouble() < 0.8 ? "ACTIVE" : "INACTIVE";
                    OffsetDateTime createdAt = OffsetDateTime.now().minusDays(r.nextInt(0, 365));

                    ps.setLong(1, sellerId);
                    ps.setString(2, name);
                    ps.setLong(3, price);
                    ps.setInt(4, stock);
                    ps.setString(5, status);
                    ps.setTimestamp(6, Timestamp.from(createdAt.toInstant()));
                }

                @Override
                public int getBatchSize() {
                    return size;
                }
            });
        }
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
