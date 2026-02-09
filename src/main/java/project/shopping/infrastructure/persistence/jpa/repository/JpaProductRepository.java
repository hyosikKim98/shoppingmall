package project.shopping.infrastructure.persistence.jpa.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import project.shopping.domain.order.port.out.ProductStockRepository;
import project.shopping.domain.product.dto.ProductSearchCondition;
import project.shopping.domain.product.model.Product;
import project.shopping.domain.product.port.out.ProductRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class JpaProductRepository implements ProductRepository, ProductStockRepository {
    @Override
    public Product save(Product product) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public Optional<Product> findById(Long productId) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public List<Product> search(ProductSearchCondition cond) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public void update(Product product) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public void deleteById(Long productId) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public boolean isOwnedBySeller(Long productId, Long sellerId) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public boolean decreaseStockIfEnough(Long productId, int quantity) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public long getCurrentPrice(Long productId) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }
}
