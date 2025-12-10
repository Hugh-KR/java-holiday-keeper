package com.planitsquare.holiday_keeper.api.dto.response;

import java.time.LocalDate;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공휴일 정보")
public record HolidayResponse(@Schema(description = "공휴일 ID") Long id,
        @Schema(description = "국가 코드", example = "KR") String countryCode,
        @Schema(description = "국가명", example = "South Korea") String countryName,
        @Schema(description = "연도", example = "2024") Integer year,
        @Schema(description = "날짜", example = "2024-01-01") LocalDate date,
        @Schema(description = "공휴일 이름 (영문)", example = "New Year's Day") String name,
        @Schema(description = "공휴일 이름 (현지어)", example = "신정") String localName,
        @Schema(description = "공휴일 타입", example = "Public") String types,
        @Schema(description = "고정 공휴일 여부", example = "true") Boolean fixed,
        @Schema(description = "전 세계 공휴일 여부", example = "false") Boolean global,
        @Schema(description = "공휴일 시작 연도", example = "1949") String launchYear) {
    public static HolidayResponse from(final PublicHoliday holiday) {
        return new HolidayResponse(holiday.getId(), holiday.getCountryCode(),
                holiday.getCountryName(), holiday.getYear(), holiday.getDate(), holiday.getName(),
                holiday.getLocalName(), holiday.getTypes(), holiday.getFixed(), holiday.getGlobal(),
                holiday.getLaunchYear());
    }
}
