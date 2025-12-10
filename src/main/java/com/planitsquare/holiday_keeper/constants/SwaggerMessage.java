package com.planitsquare.holiday_keeper.constants;

public interface SwaggerMessage {

    String HOLIDAY_API_NAME = "Holiday API";
    String HOLIDAY_API_DESCRIPTION = "공휴일 관리 API";

    String LOAD_ALL_SUMMARY = "전체 공휴일 데이터 적재";
    String SEARCH_SUMMARY = "공휴일 검색";
    String REFRESH_SUMMARY = "공휴일 데이터 재동기화";
    String DELETE_SUMMARY = "공휴일 데이터 삭제";

    String LOAD_ALL_DESCRIPTION = "2020-2025년 모든 국가의 공휴일 데이터를 일괄 적재합니다";
    String SEARCH_DESCRIPTION = "다양한 필터 조건으로 공휴일을 검색합니다 (페이징 지원)";
    String REFRESH_DESCRIPTION = "특정 연도와 국가의 공휴일 데이터를 외부 API에서 재조회하여 " + "업데이트합니다";
    String DELETE_DESCRIPTION = "특정 연도와 국가의 공휴일 데이터를 삭제합니다";

    String PARAM_YEAR = "연도";
    String PARAM_COUNTRY_CODE = "국가 코드";
    String PARAM_YEAR_EXAMPLE = "2024";
    String PARAM_COUNTRY_CODE_EXAMPLE = "KR";
}
