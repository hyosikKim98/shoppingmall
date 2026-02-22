package project.shopping.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import project.shopping.common.util.TraceIdUtil;

@Aspect
@Slf4j
public class TraceLoggingAspect {

    @Around("execution(* project.shopping.domain..api..*(..)) || execution(* project.shopping.domain..service..*(..))")
    public Object logWithTraceId(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = TraceIdUtil.ensureTraceId();
        String signature = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        log.info("[{}] START {}", traceId, signature);

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[{}] END {} ({} ms)", traceId, signature, elapsed);
            return result;
        } catch (Throwable t) {
            long elapsed = System.currentTimeMillis() - start;
            log.info("[{}] ERROR {} ({} ms) - {}", traceId, signature, elapsed, t.toString());
            throw t;
        }
    }
}
