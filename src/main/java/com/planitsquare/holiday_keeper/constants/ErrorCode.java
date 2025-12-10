package com.planitsquare.holiday_keeper.constants;

public enum ErrorCode {
    COUNTRY_NOT_FOUND("COUNTRY_001", "국가를 찾을 수 없습니다"),

    VALIDATION_FAILED("VALIDATION_001", "유효성 검증에 실패했습니다"),

    INVALID_PARAMETER("VALIDATION_002", "잘못된 파라미터입니다"),

    EXTERNAL_API_ERROR("EXTERNAL_001", "외부 API 호출에 실패했습니다"),

    EXTERNAL_API_COUNTRIES_FAILED("EXTERNAL_002", "국가 목록 조회에 실패했습니다"),

    EXTERNAL_API_HOLIDAYS_FAILED("EXTERNAL_003", "공휴일 정보 조회에 실패했습니다"),

    INTERNAL_SERVER_ERROR("SERVER_001", "서버 내부 오류가 발생했습니다"),

    UNEXPECTED_ERROR("SERVER_002", "예상치 못한 오류가 발생했습니다");

    private final String code;
    private final String message;

    ErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
