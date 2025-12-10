package com.planitsquare.holiday_keeper.constants;

public enum ErrorMessage {
    COUNTRY_NOT_FOUND("국가 코드 '%s'를 찾을 수 없습니다"),

    EXTERNAL_API_COUNTRIES_FAILED("외부 API에서 국가 목록을 가져오는데 실패했습니다: %s"),

    EXTERNAL_API_HOLIDAYS_FAILED("외부 API에서 공휴일 정보를 가져오는데 실패했습니다 (%s/%d): %s"),

    VALIDATION_FAILED("유효성 검증 실패"),

    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다: %s"),

    UNEXPECTED_ERROR("예상치 못한 오류가 발생했습니다"),

    YEAR_MUST_BE_AFTER("연도는 %d년 이후여야 합니다");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
