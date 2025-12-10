package com.planitsquare.holiday_keeper.api.controller;

import static com.planitsquare.holiday_keeper.constants.ErrorMessage.VALIDATION_FIELDS_SEPARATOR;
import static com.planitsquare.holiday_keeper.constants.ErrorMessage.VALIDATION_FIELD_SEPARATOR;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_EXCEPTION;
import static com.planitsquare.holiday_keeper.constants.LogMessage.ILLEGAL_ARGUMENT_EXCEPTION;
import static com.planitsquare.holiday_keeper.constants.LogMessage.RUNTIME_EXCEPTION;
import static com.planitsquare.holiday_keeper.constants.LogMessage.UNEXPECTED_EXCEPTION;
import static com.planitsquare.holiday_keeper.constants.LogMessage.VALIDATION_EXCEPTION;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.planitsquare.holiday_keeper.api.dto.response.ApiResponse;
import com.planitsquare.holiday_keeper.constants.ErrorCode;
import com.planitsquare.holiday_keeper.constants.ErrorMessage;
import com.planitsquare.holiday_keeper.exception.CountryNotFoundException;
import com.planitsquare.holiday_keeper.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CountryNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCountryNotFoundException(
            final CountryNotFoundException e, final WebRequest request) {
        log.warn(ILLEGAL_ARGUMENT_EXCEPTION.getMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(
                ErrorCode.COUNTRY_NOT_FOUND.getCode(), e.getMessage(), extractPath(request)));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiResponse<?>> handleExternalApiException(final ExternalApiException e,
            final WebRequest request) {
        log.error(EXTERNAL_API_EXCEPTION.getMessage(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiResponse.error(
                ErrorCode.EXTERNAL_API_ERROR.getCode(), e.getMessage(), extractPath(request)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
            final IllegalArgumentException e, final WebRequest request) {
        log.warn(ILLEGAL_ARGUMENT_EXCEPTION.getMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(
                ErrorCode.INVALID_PARAMETER.getCode(), e.getMessage(), extractPath(request)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            final MethodArgumentNotValidException e, final WebRequest request) {
        log.warn(VALIDATION_EXCEPTION.getMessage(), e.getMessage());
        final String message = extractValidationErrorMessage(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse
                .error(ErrorCode.VALIDATION_FAILED.getCode(), message, extractPath(request)));
    }

    private String extractValidationErrorMessage(final MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + VALIDATION_FIELD_SEPARATOR.getMessage()
                        + error.getDefaultMessage())
                .reduce((a, b) -> a + VALIDATION_FIELDS_SEPARATOR.getMessage() + b)
                .orElse(ErrorMessage.VALIDATION_FAILED.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(final RuntimeException e,
            final WebRequest request) {
        log.error(RUNTIME_EXCEPTION.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ErrorMessage.INTERNAL_SERVER_ERROR.getMessage().formatted(e.getMessage()),
                        extractPath(request)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(final Exception e,
            final WebRequest request) {
        log.error(UNEXPECTED_EXCEPTION.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.UNEXPECTED_ERROR.getCode(),
                        ErrorMessage.UNEXPECTED_ERROR.getMessage(), extractPath(request)));
    }

    private String extractPath(final WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
