package project.shopping.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig {

    // Lettuce direct version (disabled)
    /*
    @Bean(destroyMethod = "shutdown")
    public io.lettuce.core.RedisClient redisClient(
            @Value("${redis.host:localhost}") String host,
            @Value("${redis.port:6379}") int port
    ) {
        return io.lettuce.core.RedisClient.create(io.lettuce.core.RedisURI.Builder.redis(host).withPort(port).build());
    }

    @Bean(destroyMethod = "close")
    public io.lettuce.core.api.StatefulRedisConnection<String, String> redisConnection(io.lettuce.core.RedisClient client) {
        return client.connect(io.lettuce.core.codec.StringCodec.UTF8);
    }

    @Bean
    public io.lettuce.core.api.sync.RedisCommands<String, String> redisCommands(
            io.lettuce.core.api.StatefulRedisConnection<String, String> connection
    ) {
        return connection.sync();
    }
    */

    @Bean
    public RedisConnectionFactory redisConnectionFactory(
            @Value("${redis.host:localhost}") String host,
            @Value("${redis.port:6379}") int port
    ) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
