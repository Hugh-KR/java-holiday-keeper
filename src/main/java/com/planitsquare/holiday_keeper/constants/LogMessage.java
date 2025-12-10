package com.planitsquare.holiday_keeper.constants;

public enum LogMessage {
    LOAD_ALL_START("전체 공휴일 데이터 적재를 시작합니다 ({}년 ~ {}년)"),

    LOAD_ALL_REQUEST("전체 공휴일 데이터 적재 요청"),

    LOAD_ALL_COMPLETED("전체 공휴일 데이터 적재 완료: 총 {}개"),

    COUNTRIES_FOUND("총 {}개 국가를 발견했습니다"),

    LOAD_HOLIDAYS_COMPLETED("{}년 {} 공휴일 {}개 적재 완료"),

    LOAD_HOLIDAYS_FAILED("{}년 {} 공휴일 적재 실패: {}"),

    LOAD_HOLIDAYS_EMPTY("{}년 {} 공휴일 데이터가 없습니다"),

    REFRESH_START("{}년 {} 공휴일 데이터 재동기화를 시작합니다"),

    REFRESH_REQUEST("{}년 {} 공휴일 데이터 재동기화 요청"),

    DELETE_REQUEST("{}년 {} 공휴일 데이터 삭제 요청"),

    DELETE_COMPLETED("{}년 {} 공휴일 데이터 삭제 완료"),

    SEARCH_REQUEST("공휴일 검색 요청: {}"),

    SYNC_START("공휴일 데이터 자동 동기화 작업을 시작합니다"),

    SYNC_YEARS("{}년과 {}년 데이터를 동기화합니다"),

    SYNC_COMPLETED("{}년 {} 동기화 완료: {}개"),

    SYNC_FAILED("{}년/{}년 {} 동기화 실패: {}"),

    SYNC_ALL_COMPLETED("공휴일 데이터 자동 동기화 작업 완료: 총 {}개 동기화"),

    SYNC_ERROR("공휴일 데이터 자동 동기화 작업 중 오류 발생"),

    EXTERNAL_API_COUNTRIES_REQUEST("Requesting countries from: {}"),

    EXTERNAL_API_COUNTRIES_SUCCESS("Successfully fetched {} countries"),

    EXTERNAL_API_COUNTRIES_FAILED("Failed to fetch countries from Nager.Date API"),

    EXTERNAL_API_HOLIDAYS_REQUEST("Requesting holidays from: {}"),

    EXTERNAL_API_HOLIDAYS_SUCCESS("Successfully fetched {} holidays for {}/{}"),

    EXTERNAL_API_HOLIDAYS_FAILED("Failed to fetch holidays from Nager.Date API for {}/{}"),

    EXTERNAL_API_RETRY("외부 API 재시도: {} - {}"),

    EXTERNAL_API_RETRY_SKIPPED("외부 API 재시도 모두 실패, 요청 건너뜀: {}"),

    EXTERNAL_API_OPERATION_COUNTRIES("Nager.Date API 국가 목록 조회"),

    EXTERNAL_API_OPERATION_HOLIDAYS("Nager.Date API 공휴일 조회"),

    EXTERNAL_API_RETRY_STATUS("재시도 중"),

    ILLEGAL_ARGUMENT_EXCEPTION("잘못된 파라미터: {}"),

    VALIDATION_EXCEPTION("유효성 검증 실패: {}"),

    RUNTIME_EXCEPTION("런타임 예외 발생"),

    UNEXPECTED_EXCEPTION("예상치 못한 예외 발생"),

    EXTERNAL_API_EXCEPTION("ExternalApiException: {}"),

    COUNTRY_NOT_FOUND_EXCEPTION("국가를 찾을 수 없습니다: {}");

    private final String message;

    LogMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
