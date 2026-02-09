package project.shopping.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "Invalid request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "Not found"),
    CONFLICT(HttpStatus.CONFLICT, "COMMON_409", "Conflict"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "COMMON_429", "Too many requests"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "Internal server error"),

    // Auth/JWT
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_401_1", "Invalid token"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_401_2", "Token expired"),

    // Business examples
    OUT_OF_STOCK(HttpStatus.CONFLICT, "ORDER_409_1", "Out of stock");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() { return status; }
    public String code() { return code; }
    public String defaultMessage() { return defaultMessage; }
}
