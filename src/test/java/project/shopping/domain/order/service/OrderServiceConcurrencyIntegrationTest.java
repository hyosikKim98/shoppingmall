package project.shopping.domain.order.service;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.shopping.TestLockRepositoryConfig;
import project.shopping.common.exception.BusinessException;
import project.shopping.common.exception.ErrorCode;
import project.shopping.domain.order.dto.OrderCreateRequest;
import project.shopping.domain.order.dto.OrderItemRequest;
import project.shopping.domain.product.model.Product;
import project.shopping.infrastructure.persistence.mybatis.mapper.ProductMapper;
import project.shopping.infrastructure.persistence.mybatis.repository.MyBatisOrderRepository;
import project.shopping.infrastructure.persistence.mybatis.repository.MyBatisProductRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Import({OrderService.class, MyBatisOrderRepository.class, MyBatisProductRepository.class, TestLockRepositoryConfig.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class OrderServiceConcurrencyIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductMapper productMapper;

    @Test
    void concurrentOrders_numericProof_noOversell() throws Exception {
        int initialStock = 100;
        int requestCount = 140;
        int threadCount = 32;

        Product product = Product.createNew(1L, "LoadTest-Phone", 1000L, initialStock);
        productMapper.insert(product);

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(requestCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requestCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger outOfStock = new AtomicInteger();
        AtomicInteger others = new AtomicInteger();

        for (int i = 0; i < requestCount; i++) {
            final long customerId = 1000L + i;
            pool.submit(() -> {
                try {
                    ready.countDown();
                    start.await();

                    OrderCreateRequest req = new OrderCreateRequest(
                            List.of(new OrderItemRequest(product.getId(), 1))
                    );
                    orderService.create(customerId, req);
                    success.incrementAndGet();
                } catch (BusinessException e) {
                    if (e.errorCode() == ErrorCode.OUT_OF_STOCK || e.errorCode() == ErrorCode.TOO_MANY_REQUESTS) {
                        outOfStock.incrementAndGet();
                    } else {
                        others.incrementAndGet();
                    }
                } catch (Exception e) {
                    others.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        done.await(70, TimeUnit.SECONDS);
        pool.shutdownNow();

        int finalStock = productMapper.findById(product.getId()).orElseThrow().getStock();
        System.out.printf(
                "concurrency-result initial=%d requests=%d success=%d rejected=%d others=%d finalStock=%d%n",
                initialStock, requestCount, success.get(), outOfStock.get(), others.get(), finalStock
        );

        assertThat(outOfStock.get() + success.get() + others.get()).isEqualTo(requestCount);
        assertThat(success.get()).isLessThanOrEqualTo(initialStock);
        assertThat(finalStock).isEqualTo(initialStock - success.get());
        assertThat(finalStock).isGreaterThanOrEqualTo(0);
    }
}
