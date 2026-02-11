package project.shopping.infrastructure.persistence.mybatis;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import project.shopping.domain.product.model.Product;
import project.shopping.domain.product.model.ProductStatus;
import project.shopping.infrastructure.persistence.mybatis.mapper.ProductMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void insertFindUpdateDeleteFlow() {
        Product p = Product.createNew(10L, "Phone", 1000L, 5);

        int inserted = productMapper.insert(p);
        assertThat(inserted).isEqualTo(1);
        assertThat(p.getId()).isNotNull();

        Optional<Product> found = productMapper.findById(p.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Phone");

        p.update("Phone-2", 1200L, 7, ProductStatus.ACTIVE);
        int updated = productMapper.update(p);
        assertThat(updated).isEqualTo(1);

        Optional<Product> updatedFound = productMapper.findById(p.getId());
        assertThat(updatedFound).isPresent();
        assertThat(updatedFound.get().getPrice()).isEqualTo(1200L);
        assertThat(updatedFound.get().getStock()).isEqualTo(7);

        boolean owned = productMapper.isOwnedBySeller(p.getId(), 10L);
        assertThat(owned).isTrue();

        int deleted = productMapper.deleteById(p.getId());
        assertThat(deleted).isEqualTo(1);
    }

    @Test
    void searchAndStockDecrease() {
        Product p1 = Product.createNew(1L, "Phone", 1000L, 5);
        Product p2 = Product.createNew(1L, "Laptop", 2000L, 3);
        productMapper.insert(p1);
        productMapper.insert(p2);

        List<Product> result = productMapper.search("phone", null, null, "createdAt,desc", 20, 0);
        assertThat(result).extracting(Product::getName).contains("Phone");

        int decreased = productMapper.decreaseStockIfEnough(p1.getId(), 2);
        assertThat(decreased).isEqualTo(1);

        Optional<Product> after = productMapper.findById(p1.getId());
        assertThat(after).isPresent();
        assertThat(after.get().getStock()).isEqualTo(3);

        Long price = productMapper.getCurrentPrice(p1.getId());
        assertThat(price).isEqualTo(1000L);
    }
}
