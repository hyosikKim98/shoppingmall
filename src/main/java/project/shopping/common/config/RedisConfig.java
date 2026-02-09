package project.shopping.common.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient(
            @Value("${redis.host:localhost}") String host,
            @Value("${redis.port:6379}") int port
    ) {
        return RedisClient.create(RedisURI.Builder.redis(host).withPort(port).build());
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> redisConnection(RedisClient client) {
        return client.connect(StringCodec.UTF8);
    }

    @Bean
    public RedisCommands<String, String> redisCommands(StatefulRedisConnection<String, String> connection) {
        return connection.sync();
    }
}
