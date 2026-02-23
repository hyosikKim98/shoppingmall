package project.shopping.common.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.shopping.common.aop.TraceLoggingAspect;
import project.shopping.infrastructure.persistence.mybatis.mapper.ProductMapper;

@Configuration
public class AopConfig {

    @Bean
    public TraceLoggingAspect traceLoggingAspect() {
        return new TraceLoggingAspect();
    }

//    @Bean
//    public CountedAspect countedAspect(MeterRegistry meterRegistry) {
//        return new CountedAspect(meterRegistry);
//    }
    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }

    @Bean
    public MeterBinder stockMetrics(ProductMapper productMapper) {
        return registry -> {
            registry.gauge("my.stock.total", productMapper, ProductMapper::sumActiveStock);
            registry.gauge("my.stock.low.count", productMapper, mapper -> mapper.countActiveLowStock(10));
            registry.gauge("my.stock.zero.count", productMapper, ProductMapper::countActiveOutOfStock);
        };
    }
}
