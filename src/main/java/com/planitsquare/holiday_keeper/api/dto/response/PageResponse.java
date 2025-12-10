package com.planitsquare.holiday_keeper.api.dto.response;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "페이징 응답")
public record PageResponse<T>(@Schema(description = "데이터 목록") List<T> content,
        @Schema(description = "현재 페이지 번호 (0부터 시작)") Integer page,
        @Schema(description = "페이지 크기") Integer size,
        @Schema(description = "전체 요소 개수") Long totalElements,
        @Schema(description = "전체 페이지 수") Integer totalPages,
        @Schema(description = "첫 페이지 여부") Boolean first,
        @Schema(description = "마지막 페이지 여부") Boolean last) {
    public static <T> PageResponse<T> of(List<T> content, Integer page, Integer size,
            Long totalElements, Integer totalPages, Boolean first, Boolean last) {
        return new PageResponse<>(content, page, size, totalElements, totalPages, first, last);
    }
}
