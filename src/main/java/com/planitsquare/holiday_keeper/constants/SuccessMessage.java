package com.planitsquare.holiday_keeper.constants;

public enum SuccessMessage {
    LOAD_ALL_COMPLETED("전체 공휴일 데이터 적재가 완료되었습니다"),

    REFRESH_COMPLETED("%d년 %s 공휴일 %d개가 재동기화되었습니다"),

    DELETE_COMPLETED("%d년 %s 공휴일 데이터가 삭제되었습니다"),

    REQUEST_PROCESSED("요청이 성공적으로 처리되었습니다");

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
