package project.shopping.infrastructure.persistence.redis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Assertions.assertThat;

class RedisLockServiceFailureRecoveryTest {

    @Test
    void redisDown_thenRecovery_byEndpoint() {
        RedisLockService downService = new RedisLockService(
                newTemplate("localhost", 6390),
                3000, 2, 10, 50
        );
        String downToken = downService.tryLock(1L);
        assertThat(downToken).isNull();

        Assumptions.assumeTrue(
                DockerClientFactory.instance().isDockerAvailable(),
                "Docker is not available in this environment"
        );

        try (GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379)) {
            redis.start();

            RedisLockService upService = new RedisLockService(
                    newTemplate(redis.getHost(), redis.getMappedPort(6379)),
                    3000, 2, 10, 50
            );

            String upToken = upService.tryLock(1L);
            assertThat(upToken).isNotNull();
            assertThat(upService.unlock(1L, upToken)).isTrue();
        }
    }

    private StringRedisTemplate newTemplate(String host, int port) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(host, port);
        factory.afterPropertiesSet();

        StringRedisTemplate template = new StringRedisTemplate(factory);
        template.afterPropertiesSet();
        return template;
    }
}
