package com.planitsquare.holiday_keeper.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.planitsquare.holiday_keeper.api.dto.response.HolidayResponse;
import com.planitsquare.holiday_keeper.api.dto.response.PageResponse;
import com.planitsquare.holiday_keeper.service.HolidayService;

@WebMvcTest(HolidayController.class)
@ActiveProfiles("test")
@DisplayName("HolidayController 테스트")
class HolidayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("removal")
    private HolidayService holidayService;

    @Test
    @DisplayName("전체 공휴일 데이터 적재 API 테스트")
    void loadAllHolidays_Success() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/holidays/load").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("공휴일 검색 API 테스트")
    void searchHolidays_Success() throws Exception {
        // given
        PageResponse<HolidayResponse> response =
                PageResponse.of(Collections.emptyList(), 0, 20, 0L, 0, true, true);

        when(holidayService.searchHolidays(any())).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/holidays/search").param("countryCode", "KR")
                .param("year", "2024").param("page", "0").param("size", "20")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("공휴일 재동기화 API 테스트")
    void refreshHolidays_Success() throws Exception {
        // given
        when(holidayService.refreshHolidays(2024, "KR")).thenReturn(10);

        // when & then
        mockMvc.perform(
                put("/api/v1/holidays/refresh/2024/KR").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("공휴일 삭제 API 테스트")
    void deleteHolidays_Success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/holidays/2024/KR").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }
}
