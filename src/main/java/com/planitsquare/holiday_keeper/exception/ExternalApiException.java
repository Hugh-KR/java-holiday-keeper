package com.planitsquare.holiday_keeper.exception;

import static com.planitsquare.holiday_keeper.constants.ErrorMessage.EXTERNAL_API_CALL_FAILED;

public class ExternalApiException extends RuntimeException {

    private final String operation;
    private final String details;

    public ExternalApiException(final String operation, final String details) {
        super(EXTERNAL_API_CALL_FAILED.getMessage().formatted(operation, details));
        this.operation = operation;
        this.details = details;
    }

    public ExternalApiException(final String operation, final String details,
            final Throwable cause) {
        super(EXTERNAL_API_CALL_FAILED.getMessage().formatted(operation, details), cause);
        this.operation = operation;
        this.details = details;
    }

    public String getOperation() {
        return operation;
    }

    public String getDetails() {
        return details;
    }
}
