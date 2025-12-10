package com.planitsquare.holiday_keeper.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.planitsquare.holiday_keeper.api.dto.request.HolidaySearchRequest;
import com.planitsquare.holiday_keeper.api.dto.response.ApiResponse;
import com.planitsquare.holiday_keeper.api.dto.response.HolidayResponse;
import com.planitsquare.holiday_keeper.api.dto.response.PageResponse;
import com.planitsquare.holiday_keeper.constants.LogMessage;
import com.planitsquare.holiday_keeper.constants.SuccessMessage;
import com.planitsquare.holiday_keeper.constants.SwaggerMessage;
import com.planitsquare.holiday_keeper.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/holidays")
@RequiredArgsConstructor
@Tag(name = SwaggerMessage.HOLIDAY_API_NAME, description = SwaggerMessage.HOLIDAY_API_DESCRIPTION)
public class HolidayController {

    private final HolidayService holidayService;

    @PostMapping("/load")
    @Operation(summary = SwaggerMessage.LOAD_ALL_SUMMARY,
            description = SwaggerMessage.LOAD_ALL_DESCRIPTION)
    public ResponseEntity<ApiResponse<String>> loadAllHolidays() {
        log.info(LogMessage.LOAD_ALL_REQUEST.getMessage());
        holidayService.loadAllHolidays();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessMessage.LOAD_ALL_COMPLETED.getMessage(), null));
    }

    @GetMapping("/search")
    @Operation(summary = SwaggerMessage.SEARCH_SUMMARY,
            description = SwaggerMessage.SEARCH_DESCRIPTION)
    public ResponseEntity<ApiResponse<PageResponse<HolidayResponse>>> searchHolidays(
            @Valid @ModelAttribute final HolidaySearchRequest request) {
        log.info(LogMessage.SEARCH_REQUEST.getMessage(), request);
        final PageResponse<HolidayResponse> result = holidayService.searchHolidays(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/refresh/{year}/{countryCode}")
    @Operation(summary = SwaggerMessage.REFRESH_SUMMARY,
            description = SwaggerMessage.REFRESH_DESCRIPTION)
    public ResponseEntity<ApiResponse<String>> refreshHolidays(
            @Parameter(description = SwaggerMessage.PARAM_YEAR,
                    example = SwaggerMessage.PARAM_YEAR_EXAMPLE,
                    required = true) @PathVariable final Integer year,
            @Parameter(description = SwaggerMessage.PARAM_COUNTRY_CODE,
                    example = SwaggerMessage.PARAM_COUNTRY_CODE_EXAMPLE,
                    required = true) @PathVariable final String countryCode) {
        log.info(LogMessage.REFRESH_REQUEST.getMessage(), year, countryCode);
        final Integer count = holidayService.refreshHolidays(year, countryCode);
        return ResponseEntity
                .ok(ApiResponse.success(String.format(SuccessMessage.REFRESH_COMPLETED.getMessage(),
                        year, countryCode, count), null));
    }

    @DeleteMapping("/{year}/{countryCode}")
    @Operation(summary = SwaggerMessage.DELETE_SUMMARY,
            description = SwaggerMessage.DELETE_DESCRIPTION)
    public ResponseEntity<ApiResponse<String>> deleteHolidays(
            @Parameter(description = SwaggerMessage.PARAM_YEAR,
                    example = SwaggerMessage.PARAM_YEAR_EXAMPLE,
                    required = true) @PathVariable final Integer year,
            @Parameter(description = SwaggerMessage.PARAM_COUNTRY_CODE,
                    example = SwaggerMessage.PARAM_COUNTRY_CODE_EXAMPLE,
                    required = true) @PathVariable final String countryCode) {
        log.info(LogMessage.DELETE_REQUEST.getMessage(), year, countryCode);
        holidayService.deleteHolidays(year, countryCode);
        return ResponseEntity.ok(ApiResponse.success(
                String.format(SuccessMessage.DELETE_COMPLETED.getMessage(), year, countryCode),
                null));
    }
}
