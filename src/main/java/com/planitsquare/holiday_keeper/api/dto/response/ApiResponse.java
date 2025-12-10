package com.planitsquare.holiday_keeper.api.dto.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(@Schema(description = "성공 여부", example = "true") Boolean success,
        @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다") String message,
        @Schema(description = "응답 데이터") T data,
        @Schema(description = "에러 코드", example = "COUNTRY_001") String errorCode,
        @Schema(description = "타임스탬프", example = "2024-12-10T10:30:00") LocalDateTime timestamp,
        @Schema(description = "요청 경로", example = "/api/v1/holidays/search") String path) {

    public static <T> ApiResponse<T> success(final T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다", data, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> success(final String message, final T data) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(final String message) {
        return new ApiResponse<>(false, message, null, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(final String errorCode, final String message) {
        return new ApiResponse<>(false, message, null, errorCode, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(final String errorCode, final String message,
            final String path) {
        return new ApiResponse<>(false, message, null, errorCode, LocalDateTime.now(), path);
    }
}
