package com.planitsquare.holiday_keeper.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.planitsquare.holiday_keeper.exception.CountryNotFoundException;
import com.planitsquare.holiday_keeper.exception.ExternalApiException;
import com.planitsquare.holiday_keeper.service.HolidayService;

@WebMvcTest(controllers = {HolidayController.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("removal")
    private HolidayService holidayService;

    @Test
    @DisplayName("IllegalArgumentException 처리 테스트")
    void handleIllegalArgumentException() throws Exception {
        // given
        when(holidayService.searchHolidays(any()))
                .thenThrow(new IllegalArgumentException("국가 코드 'INVALID'를 찾을 수 없습니다"));
        final org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request =
                get("/api/v1/holidays/search").param("countryCode", "INVALID").param("year", "2024")
                        .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists()).andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 테스트 - 잘못된 연도")
    void handleValidationException_InvalidYear() throws Exception {
        // given
        final org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request =
                get("/api/v1/holidays/search").param("countryCode", "KR").param("year", "1999")
                        .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists()).andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("CountryNotFoundException 처리 테스트")
    void handleCountryNotFoundException() throws Exception {
        // given
        when(holidayService.searchHolidays(any())).thenThrow(new CountryNotFoundException("XX"));
        final org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request =
                get("/api/v1/holidays/search").param("countryCode", "XX").param("year", "2024")
                        .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists()).andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("ExternalApiException 처리 테스트")
    void handleExternalApiException() throws Exception {
        // given
        when(holidayService.searchHolidays(any()))
                .thenThrow(new ExternalApiException("공휴일 조회", "외부 API 호출 실패"));
        final org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request =
                get("/api/v1/holidays/search").param("countryCode", "KR").param("year", "2024")
                        .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request).andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists()).andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("RuntimeException 처리 테스트")
    void handleRuntimeException() throws Exception {
        // given
        when(holidayService.searchHolidays(any()))
                .thenThrow(new RuntimeException("내부 서버 오류가 발생했습니다"));
        final org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request =
                get("/api/v1/holidays/search").param("countryCode", "KR").param("year", "2024")
                        .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists()).andExpect(jsonPath("$.path").exists());
    }
}
