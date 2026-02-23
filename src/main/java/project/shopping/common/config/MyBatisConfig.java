package project.shopping.common.config;

import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = "project.shopping.infrastructure.persistence.mybatis.mapper")
public class MyBatisConfig {

    @Bean
    public VendorDatabaseIdProvider databaseIdProvider(DataSource dataSource) {
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("H2", "h2");
        provider.setProperties(properties);
        return provider;
    }
}
