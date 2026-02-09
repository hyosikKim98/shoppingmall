package project.shopping.infrastructure.persistence.redis;

public class RedisLockException extends RuntimeException {
    public RedisLockException(String message) {
        super(message);
    }
}
