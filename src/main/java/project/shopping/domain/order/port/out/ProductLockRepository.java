package project.shopping.domain.order.port.out;

public interface ProductLockRepository {
    /**
     * Try to acquire lock for product. Returns token if success, otherwise null.
     */
    String tryLock(Long productId);

    /**
     * Release lock with token. Returns true if unlocked.
     */
    boolean unlock(Long productId, String token);
}
