package project.shopping.common.util;

import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

public final class TraceIdUtil {
    public static final String MDC_KEY = "traceId";

    private TraceIdUtil() {}

    public static String ensureTraceId() {
        String current = MDC.get(MDC_KEY);
        if (current != null && !current.isBlank()) return current;

        String traceId = UUID.randomUUID().toString();
        MDC.put(MDC_KEY, traceId);

        return traceId;
    }

    public static Optional<String> getTraceId() {
        return Optional.ofNullable(MDC.get(MDC_KEY));
    }

    public static void clear() {
        MDC.remove(MDC_KEY);
    }
}