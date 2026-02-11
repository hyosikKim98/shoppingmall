package project.shopping.domain.product.service;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import project.shopping.domain.product.dto.ProductCreateRequest;
import project.shopping.domain.product.dto.ProductResponse;
import project.shopping.domain.product.dto.ProductUpdateRequest;
import project.shopping.domain.product.model.Product;
import project.shopping.domain.product.model.ProductStatus;
import project.shopping.infrastructure.persistence.mybatis.mapper.ProductMapper;
import project.shopping.infrastructure.persistence.mybatis.repository.MyBatisProductRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Import({ProductService.class, MyBatisProductRepository.class})
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Test
    void createAndGet() {
        ProductResponse res = productService.create(10L, new ProductCreateRequest("Phone", 1000L, 5));
        ProductResponse fetched = productService.get(res.id());

        assertThat(fetched.name()).isEqualTo("Phone");
        assertThat(fetched.price()).isEqualTo(1000L);
        assertThat(fetched.stock()).isEqualTo(5);
    }

    @Test
    void updateAndDelete() {
        Product p = Product.createNew(10L, "A", 100L, 1);
        productMapper.insert(p);

        productService.update(10L, p.getId(), new ProductUpdateRequest("B", 200L, 3, "INACTIVE"));

        Optional<Product> updated = productMapper.findById(p.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("B");
        assertThat(updated.get().getPrice()).isEqualTo(200L);
        assertThat(updated.get().getStock()).isEqualTo(3);
        assertThat(updated.get().getStatus()).isEqualTo(ProductStatus.INACTIVE);

        productService.delete(10L, p.getId());
        assertThat(productMapper.findById(p.getId())).isEmpty();
    }
}
