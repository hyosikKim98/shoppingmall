package project.shopping.domain.product.service;

import tools.jackson.core.JacksonException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.shopping.common.exception.BusinessException;
import project.shopping.common.exception.ErrorCode;
import project.shopping.domain.product.dto.ProductCreateRequest;
import project.shopping.domain.product.dto.ProductResponse;
import project.shopping.domain.product.dto.ProductSearchCondition;
import project.shopping.domain.product.dto.ProductUpdateRequest;
import project.shopping.domain.product.model.Product;
import project.shopping.domain.product.model.ProductStatus;
import project.shopping.domain.product.port.out.ProductRepository;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String PRODUCT_CACHE_KEY_PREFIX = "product:";

    private final ProductRepository productRepository;
    private final ObjectProvider<StringRedisTemplate> redisTemplateProvider;
    private final ObjectProvider<JsonMapper> jsonMapperProvider;

    @Value("${redis.cache.product.enabled:true}")
    private boolean productCacheEnabled;

    @Value("${redis.cache.product.ttl.seconds:60}")
    private long productCacheTtlSeconds;

    @Transactional
    public ProductResponse create(Long sellerId, ProductCreateRequest req) {
        Product saved = productRepository.save(Product.createNew(sellerId, req.name(), req.price(), req.stock()));
        return ProductResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long productId) {
        String cacheKey = productCacheKey(productId);
        if (cacheAvailable()) {
            StringRedisTemplate redis = redisTemplateProvider.getIfAvailable();
            JsonMapper mapper = jsonMapperProvider.getIfAvailable();
            try {
                String cached = redis.opsForValue().get(cacheKey);
                if (cached != null) {
                    return mapper.readValue(cached, ProductResponse.class);
                }
            } catch (JacksonException e) {
                redis.delete(cacheKey);
            } catch (RuntimeException ignored) {
                // Redis 미가용 시 DB fallback
            }
        }


        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Product not found"));
        ProductResponse response = ProductResponse.from(p);

        if (cacheAvailable()) {
            StringRedisTemplate redis = redisTemplateProvider.getIfAvailable();
            JsonMapper mapper = jsonMapperProvider.getIfAvailable();
            try {
                redis.opsForValue().set(
                        cacheKey,
                        mapper.writeValueAsString(response),
                        Duration.ofSeconds(productCacheTtlSeconds)
                );
            } catch (JacksonException ignored) {
            } catch (RuntimeException ignored) {
                // Redis 미가용 시 캐시 저장 skip
            }
        }


        return response;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> search(ProductSearchCondition cond) {
        return productRepository.search(cond).stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse update(Long sellerId, Long productId, ProductUpdateRequest req) {
        if (!productRepository.isOwnedBySeller(productId, sellerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Not your product");
        }

        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Product not found"));

        ProductStatus status = null;
        if (req.status() != null) {
            status = ProductStatus.valueOf(req.status().toUpperCase(Locale.ROOT));
        }

        p.update(req.name(), req.price(), req.stock(), status);
        productRepository.update(p);
        evictProductCache(productId);

        return ProductResponse.from(p);
    }

    @Transactional
    public void delete(Long sellerId, Long productId) {
        if (!productRepository.isOwnedBySeller(productId, sellerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Not your product");
        }
        productRepository.deleteById(productId);
        evictProductCache(productId);
    }

    private String productCacheKey(Long productId) {
        return PRODUCT_CACHE_KEY_PREFIX + productId;
    }

    private void evictProductCache(Long productId) {
        if (!cacheAvailable()) return;
        try {
            redisTemplateProvider.getIfAvailable().delete(productCacheKey(productId));
        } catch (RuntimeException ignored) {
            // Redis 미가용 시 캐시 삭제 skip
        }
    }

    private boolean cacheAvailable() {
        return productCacheEnabled
                && redisTemplateProvider.getIfAvailable() != null
                && jsonMapperProvider.getIfAvailable() != null;
    }


}
