package project.shopping.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String traceId,
        String errorCode,
        String message
) {

    public static <T> ApiResponse<T> ok(T data, String traceId) {
        return new ApiResponse<>(true, data, traceId, null, null);
    }

    public static ApiResponse<Void> ok(String traceId) {
        return new ApiResponse<>(true, null, traceId, null, null);
    }

    public static ApiResponse<Void> fail(String errorCode, String message, String traceId) {
        return new ApiResponse<>(false, null, traceId, errorCode, message);
    }
}