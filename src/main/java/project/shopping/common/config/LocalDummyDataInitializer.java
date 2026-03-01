package project.shopping.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.shopping.domain.product.dto.ProductSearchCondition;
import project.shopping.domain.product.model.Product;
import project.shopping.domain.product.port.out.ProductRepository;
import project.shopping.domain.user.model.Role;
import project.shopping.domain.user.model.User;
import project.shopping.infrastructure.persistence.mybatis.mapper.UserMapper;

@Component
@Profile("local")
@ConditionalOnProperty(name = "seed.local-dummy.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class LocalDummyDataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User seller = ensureSeller();
        seedProducts(seller.getId());
    }

    private User ensureSeller() {
        String sellerEmail = "seller@example.com";
        return userMapper.findByEmail(sellerEmail).orElseGet(() -> {
            String hash = passwordEncoder.encode("1234");
            User newSeller = User.createNew(sellerEmail, hash, Role.SELLER);
            userMapper.insert(newSeller);
            return newSeller;
        });
    }

    private void seedProducts(Long sellerId) {
        // 이미 상품이 있으면 중복 생성 방지
        if (!productRepository.search(new ProductSearchCondition(
                null, null, null, "createdAt,desc", 0, 1
        )).isEmpty()) {
            return;
        }

        productRepository.save(Product.createNew(sellerId, "Wireless Mouse", 25000L, 120));
        productRepository.save(Product.createNew(sellerId, "Mechanical Keyboard", 89000L, 80));
        productRepository.save(Product.createNew(sellerId, "USB-C Hub", 39000L, 60));
        productRepository.save(Product.createNew(sellerId, "27-inch Monitor", 229000L, 35));
        productRepository.save(Product.createNew(sellerId, "Laptop Stand", 31000L, 90));
        productRepository.save(Product.createNew(sellerId, "Webcam FHD", 54000L, 50));
        productRepository.save(Product.createNew(sellerId, "Bluetooth Speaker", 67000L, 70));
        productRepository.save(Product.createNew(sellerId, "Noise Cancelling Headphones", 159000L, 40));
        productRepository.save(Product.createNew(sellerId, "Portable SSD 1TB", 149000L, 55));
        productRepository.save(Product.createNew(sellerId, "Smart Plug", 19000L, 200));
    }
}
