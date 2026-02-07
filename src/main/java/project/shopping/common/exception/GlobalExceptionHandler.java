package project.shopping.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.shopping.common.util.TraceIdUtil;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        String traceId = TraceIdUtil.ensureTraceId();
        ErrorCode code = e.errorCode();
        return ResponseEntity
                .status(code.status())
                .body(ErrorResponse.of(code, e.getMessage(), traceId));
    }

    /**
     *Bean validation 위반
     *@NotNull, @Size 등의 제약 조건을 위반한 데이터
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String traceId = TraceIdUtil.ensureTraceId();
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST, msg, traceId));
    }

    /**
     * JPA 검증 단계에서 발생
     * 데이터베이스 제약 조건(Unique, Not Null, Foreign Key 등)을 위반하거나,
     * JPA/Bean Validation 제약(데이터 형식, 길이 등)을 위반할 때 발생하는 예외
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException e) {
        String traceId = TraceIdUtil.ensureTraceId();
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST, e.getMessage(), traceId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        String traceId = TraceIdUtil.ensureTraceId();
        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.status())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.defaultMessage(), traceId));
    }
}
