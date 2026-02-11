package project.shopping.infrastructure.persistence.mybatis;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import project.shopping.domain.user.model.Role;
import project.shopping.domain.user.model.User;
import project.shopping.infrastructure.persistence.mybatis.mapper.UserMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void insertAndFindByEmail() {
        User user = User.createNew("a@test.com", "hash", Role.CUSTOMER);

        int rows = userMapper.insert(user);
        assertThat(rows).isEqualTo(1);
        assertThat(user.getId()).isNotNull();

        Optional<User> found = userMapper.findByEmail("a@test.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("a@test.com");

        boolean exists = userMapper.existsByEmail("a@test.com");
        assertThat(exists).isTrue();
    }
}
