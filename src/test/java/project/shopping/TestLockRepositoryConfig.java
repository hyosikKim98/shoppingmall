package project.shopping;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import project.shopping.domain.order.port.out.ProductLockRepository;

import java.util.UUID;

@TestConfiguration
public class TestLockRepositoryConfig {

    @Bean
    @Primary
    public ProductLockRepository productLockRepository() {
        return new ProductLockRepository() {
            @Override
            public String tryLock(Long productId) {
                return UUID.randomUUID().toString();
            }

            @Override
            public boolean unlock(Long productId, String token) {
                return true;
            }
        };
    }
}
