package com.planitsquare.holiday_keeper.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.planitsquare.holiday_keeper.api.dto.response.ApiResponse;
import com.planitsquare.holiday_keeper.constants.ErrorCode;
import com.planitsquare.holiday_keeper.constants.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
            final IllegalArgumentException e, final WebRequest request) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(
                ErrorCode.INVALID_PARAMETER.getCode(), e.getMessage(), extractPath(request)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            final MethodArgumentNotValidException e, final WebRequest request) {
        log.warn("ValidationException: {}", e.getMessage());
        final String message = extractValidationErrorMessage(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse
                .error(ErrorCode.VALIDATION_FAILED.getCode(), message, extractPath(request)));
    }

    private String extractValidationErrorMessage(final MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b).orElse(ErrorMessage.VALIDATION_FAILED.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(final RuntimeException e,
            final WebRequest request) {
        log.error("RuntimeException: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), String
                        .format(ErrorMessage.INTERNAL_SERVER_ERROR.getMessage(), e.getMessage()),
                        extractPath(request)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(final Exception e,
            final WebRequest request) {
        log.error("Unexpected exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.UNEXPECTED_ERROR.getCode(),
                        ErrorMessage.UNEXPECTED_ERROR.getMessage(), extractPath(request)));
    }

    private String extractPath(final WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
