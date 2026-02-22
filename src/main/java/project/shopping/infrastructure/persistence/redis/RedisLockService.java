package project.shopping.infrastructure.persistence.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import project.shopping.domain.order.port.out.ProductLockRepository;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// Lettuce direct version (disabled)
/*
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;

private static final String UNLOCK_SCRIPT =
        "if redis.call('GET', KEYS[1]) == ARGV[1] then\n" +
        "  return redis.call('DEL', KEYS[1])\n" +
        "else\n" +
        "  return 0\n" +
        "end";
*/

@Component
@Slf4j
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisLockService implements ProductLockRepository {

    private static final String LOCK_PREFIX = "lock:product:";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT_SD = new DefaultRedisScript<>(
            "if redis.call('GET', KEYS[1]) == ARGV[1] then\n" +
            "  return redis.call('DEL', KEYS[1])\n" +
            "else\n" +
            "  return 0\n" +
            "end",
            Long.class
    );

    private final StringRedisTemplate redis;
    private final long lockTtlMs;
    private final int lockMaxAttempts;
    private final long lockBaseBackoffMs;
    private final long lockMaxBackoffMs;

    public RedisLockService(
            StringRedisTemplate redis,
            @Value("${redis.lock.ttl.ms}") long lockTtlMs,
            @Value("${redis.lock.retry.max}") int lockMaxAttempts,
            @Value("${redis.lock.retry.base-ms}") long lockBaseBackoffMs,
            @Value("${redis.lock.retry.max-backoff-ms}") long lockMaxBackoffMs
    ) {
        this.redis = redis;
        this.lockTtlMs = lockTtlMs;
        this.lockMaxAttempts = Math.max(1, lockMaxAttempts);
        this.lockBaseBackoffMs = Math.max(1, lockBaseBackoffMs);
        this.lockMaxBackoffMs = Math.max(lockBaseBackoffMs, lockMaxBackoffMs);
    }

    @Override
    public String tryLock(Long productId) {
        String lockKey = LOCK_PREFIX + productId;
        String token = UUID.randomUUID().toString();

        for (int attempt = 1; attempt <= lockMaxAttempts; attempt++) {
            Boolean result = redis.opsForValue().setIfAbsent(
                    lockKey,
                    token,
                    java.time.Duration.ofMillis(lockTtlMs)
            );
            if (Boolean.TRUE.equals(result)) return token;

            if (attempt < lockMaxAttempts) {
                long backoff = Math.min(lockMaxBackoffMs, lockBaseBackoffMs * (1L << (attempt - 1)));
                long jitter = ThreadLocalRandom.current().nextLong(0, Math.max(1, backoff / 2));
                sleep(backoff + jitter);
            }
        }

        log.warn("Lock acquisition failed: productId={}", productId);
        return null;
    }

    @Override
    public boolean unlock(Long productId, String token) {
        String lockKey = LOCK_PREFIX + productId;
        Long result = redis.execute(UNLOCK_SCRIPT_SD, java.util.List.of(lockKey), token);
        return result != null && result > 0;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
