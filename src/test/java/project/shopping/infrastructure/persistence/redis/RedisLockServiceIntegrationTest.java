package project.shopping.infrastructure.persistence.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import project.shopping.common.config.RedisConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = {RedisConfig.class, RedisLockService.class})
class RedisLockServiceIntegrationTest {

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("redis.host", redis::getHost);
        registry.add("redis.port", () -> redis.getMappedPort(6379));
        registry.add("redis.enabled", () -> "true");
    }

    @Autowired
    private RedisLockService redisLockService;

    @Test
    void lockAcquireAndRelease() {
        String token1 = redisLockService.tryLock(1L);
        assertThat(token1).isNotNull();

        String token2 = redisLockService.tryLock(1L);
        assertThat(token2).isNull();

        boolean unlocked = redisLockService.unlock(1L, token1);
        assertThat(unlocked).isTrue();

        String token3 = redisLockService.tryLock(1L);
        assertThat(token3).isNotNull();
    }
}
