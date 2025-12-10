package com.planitsquare.holiday_keeper.api.dto.request;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "공휴일 검색 요청")
public record HolidaySearchRequest(
        @Schema(description = "국가 코드 (ISO 3166-1 alpha-2)", example = "KR") String countryCode,
        @Schema(description = "연도", example = "2024") @Min(value = 2000,
                message = "연도는 2000년 이후여야 합니다") Integer year,
        @Schema(description = "시작 날짜 (from)", example = "2024-01-01") LocalDate from,
        @Schema(description = "종료 날짜 (to)", example = "2024-12-31") LocalDate to,
        @Schema(description = "공휴일 타입", example = "Public") String types,
        @Schema(description = "페이지 번호 (0부터 시작)", example = "0") Integer page,
        @Schema(description = "페이지 크기", example = "20") Integer size) {

    private static final Integer DEFAULT_PAGE = 0;
    private static final Integer DEFAULT_SIZE = 20;

    public HolidaySearchRequest withDefaults() {
        return new HolidaySearchRequest(countryCode, year, from, to, types,
                page != null ? page : DEFAULT_PAGE, size != null ? size : DEFAULT_SIZE);
    }
}
