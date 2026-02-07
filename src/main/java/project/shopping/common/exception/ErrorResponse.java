package project.shopping.common.exception;

import java.time.OffsetDateTime;

public record ErrorResponse(
        String errorCode,
        String message,
        String traceId,
        OffsetDateTime timestamp
) {
    public static ErrorResponse of(ErrorCode code, String message, String traceId) {
        return new ErrorResponse(code.code(), message, traceId, OffsetDateTime.now());
    }
}
