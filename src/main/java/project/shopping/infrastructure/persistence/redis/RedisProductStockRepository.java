package project.shopping.infrastructure.persistence.redis;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import project.shopping.domain.order.port.out.ProductStockRepository;
import project.shopping.infrastructure.persistence.mybatis.mapper.ProductMapper;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Repository
@Slf4j
public class RedisProductStockRepository implements ProductStockRepository {

    private static final String LOCK_PREFIX = "lock:product:";

    private static final String UNLOCK_SCRIPT =
            "if redis.call('GET', KEYS[1]) == ARGV[1] then\n" +
            "  return redis.call('DEL', KEYS[1])\n" +
            "else\n" +
            "  return 0\n" +
            "end";

    private final RedisCommands<String, String> redis;
    private final ProductMapper productMapper;
    private final long lockTtlMs;
    private final int lockMaxAttempts;
    private final long lockBaseBackoffMs;
    private final long lockMaxBackoffMs;

    public RedisProductStockRepository(
            RedisCommands<String, String> redis,
            ProductMapper productMapper,
            @Value("${redis.lock.ttl.ms:3000}") long lockTtlMs,
            @Value("${redis.lock.retry.max:3}") int lockMaxAttempts,
            @Value("${redis.lock.retry.base.ms:50}") long lockBaseBackoffMs,
            @Value("${redis.lock.retry.max.backoff.ms:300}") long lockMaxBackoffMs
    ) {
        this.redis = redis;
        this.productMapper = productMapper;
        this.lockTtlMs = lockTtlMs;
        this.lockMaxAttempts = Math.max(1, lockMaxAttempts);
        this.lockBaseBackoffMs = Math.max(1, lockBaseBackoffMs);
        this.lockMaxBackoffMs = Math.max(lockBaseBackoffMs, lockMaxBackoffMs);
    }

    @Override
    public boolean decreaseStockIfEnough(Long productId, int quantity) {
        String lockKey = lockKey(productId);
        String token = UUID.randomUUID().toString();
        if (!tryLock(lockKey, token)) {
            throw new RedisLockException("Lock acquisition failed: productId=" + productId);
        }

        try {
            return productMapper.decreaseStockIfEnough(productId, quantity) > 0;
        } finally {
            boolean unlocked = unlock(lockKey, token);
            if (!unlocked) {
                log.warn("Unlock skipped (not owner or expired): key={}, productId={}", lockKey, productId);
            }
        }
    }

    @Override
    public long getCurrentPrice(Long productId) {
        Long price = productMapper.getCurrentPrice(productId);
        if (price == null) {
            throw new IllegalStateException("Product not found: productId=" + productId);
        }
        return price;
    }

    private boolean tryLock(String lockKey, String token) {
        SetArgs args = SetArgs.Builder.nx().px(lockTtlMs);
        for (int attempt = 1; attempt <= lockMaxAttempts; attempt++) {
            String result = redis.set(lockKey, token, args);
            if ("OK".equals(result)) return true;

            if (attempt < lockMaxAttempts) {
                long backoff = Math.min(lockMaxBackoffMs, lockBaseBackoffMs * (1L << (attempt - 1)));
                long jitter = ThreadLocalRandom.current().nextLong(0, Math.max(1, backoff / 2));
                sleep(backoff + jitter);
            }
        }
        log.warn("Lock acquisition failed after {} attempts: key={}", lockMaxAttempts, lockKey);
        return false;
    }

    private boolean unlock(String lockKey, String token) {
        Long result = redis.eval(UNLOCK_SCRIPT, ScriptOutputType.INTEGER, new String[]{lockKey}, token);
        return result != null && result > 0;
    }

    private String lockKey(Long productId) {
        return LOCK_PREFIX + productId;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
