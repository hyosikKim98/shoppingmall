package project.shopping.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "project.shopping.infrastructure.persistence.mybatis.mapper")
public class MyBatisConfig {
    // application.yml의 mybatis.* 설정으로 대부분 커버됨
    // (mapper-locations, map-underscore-to-camel-case 등)
}