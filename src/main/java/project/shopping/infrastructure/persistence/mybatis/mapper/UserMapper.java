package project.shopping.infrastructure.persistence.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import project.shopping.domain.user.model.User;

import java.util.Optional;

@Mapper
public interface UserMapper {
    int insert(User user);
    Optional<User> findByEmail(@Param("email") String email);
    boolean existsByEmail(@Param("email") String email);
}
