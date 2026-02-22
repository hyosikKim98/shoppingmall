package project.shopping.common.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.shopping.common.aop.TraceLoggingAspect;
import project.shopping.domain.order.service.OrderService;

@Slf4j
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

    /**
     * Prometheus 가 수집하는 주기마다 실행
     */
    @Bean
    public MeterBinder stockSize(OrderService orderService) {
        return registry -> Gauge.builder("my.stock", orderService, service ->
        {
            log.info("stock gauge call");
            return service.getStock().get();
        }).register(registry);
    }
}
