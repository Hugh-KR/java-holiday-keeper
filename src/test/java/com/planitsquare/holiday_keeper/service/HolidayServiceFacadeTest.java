package com.planitsquare.holiday_keeper.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.planitsquare.holiday_keeper.api.dto.request.HolidaySearchRequest;
import com.planitsquare.holiday_keeper.api.dto.response.HolidayResponse;
import com.planitsquare.holiday_keeper.api.dto.response.PageResponse;
import com.planitsquare.holiday_keeper.domain.entity.Country;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;
import com.planitsquare.holiday_keeper.domain.repository.HolidayRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("HolidayService 테스트")
class HolidayServiceFacadeTest {

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private HolidayDataService holidayDataService;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private HolidayService holidayService;

    private Country testCountry;
    private PublicHoliday testHoliday;

    @BeforeEach
    void setUp() {
        testCountry = Country.builder().countryCode("KR").name("South Korea").build();
        org.springframework.test.util.ReflectionTestUtils.setField(testCountry, "id", 1L);
        testHoliday = PublicHoliday.builder().country(testCountry).year(2024).countryCode("KR")
                .name("신정").localName("New Year's Day").date(java.time.LocalDate.of(2024, 1, 1))
                .types("Public").fixed(true).global(false).build();
        org.springframework.test.util.ReflectionTestUtils.setField(testHoliday, "id", 1L);
    }

    @Test
    @DisplayName("전체 공휴일 적재 성공")
    void loadAllHolidays_Success() {
        // given
        when(holidayDataService.loadAllHolidays()).thenReturn(100);

        // when
        holidayService.loadAllHolidays();

        // then
        verify(holidayDataService).loadAllHolidays();
    }

    @Test
    @DisplayName("공휴일 재동기화 성공")
    void refreshHolidays_Success() {
        // given
        when(countryService.findByCountryCode("KR")).thenReturn(testCountry);
        when(holidayDataService.loadHolidaysForYearAndCountry(2024, "KR", testCountry))
                .thenReturn(10);

        // when
        final Integer result = holidayService.refreshHolidays(2024, "KR");

        // then
        assertThat(result).isEqualTo(10);
        verify(countryService).findByCountryCode("KR");
        verify(holidayDataService).loadHolidaysForYearAndCountry(2024, "KR", testCountry);
    }

    @Test
    @DisplayName("공휴일 재동기화 실패 - 국가 코드 없음")
    void refreshHolidays_Fail_CountryNotFound() {
        // given
        doThrow(new IllegalArgumentException("국가 코드 'XX'를 찾을 수 없습니다")).when(countryService)
                .findByCountryCode("XX");

        // when & then
        assertThatThrownBy(() -> holidayService.refreshHolidays(2024, "XX"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("국가 코드 'XX'를 찾을 수 없습니다");
        verify(countryService).findByCountryCode("XX");
        verify(holidayDataService, never()).loadHolidaysForYearAndCountry(any(), any(), any());
    }

    @Test
    @DisplayName("공휴일 삭제 성공")
    void deleteHolidays_Success() {
        // given
        doNothing().when(holidayDataService).deleteHolidays(2024, "KR");

        // when
        holidayService.deleteHolidays(2024, "KR");

        // then
        verify(holidayDataService).deleteHolidays(2024, "KR");
    }

    @Test
    @DisplayName("공휴일 검색 성공")
    void searchHolidays_Success() {
        // given
        final HolidaySearchRequest request =
                new HolidaySearchRequest("KR", 2024, null, null, null, 0, 20);
        final Pageable pageable = PageRequest.of(0, 20);
        final Page<PublicHoliday> holidayPage =
                new PageImpl<>(Collections.singletonList(testHoliday), pageable, 1L);

        when(holidayRepository.search(any(HolidaySearchRequest.class), any(Pageable.class)))
                .thenReturn(holidayPage);

        // when
        final PageResponse<HolidayResponse> result = holidayService.searchHolidays(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(20);
        verify(holidayRepository).search(any(HolidaySearchRequest.class), any(Pageable.class));
    }

    @Test
    @DisplayName("공휴일 검색 성공 - 빈 결과")
    void searchHolidays_Success_EmptyResult() {
        // given
        final HolidaySearchRequest request =
                new HolidaySearchRequest("XX", 2024, null, null, null, 0, 20);
        final Pageable pageable = PageRequest.of(0, 20);
        final Page<PublicHoliday> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0L);

        when(holidayRepository.search(any(HolidaySearchRequest.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // when
        final PageResponse<HolidayResponse> result = holidayService.searchHolidays(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0L);
        verify(holidayRepository).search(any(HolidaySearchRequest.class), any(Pageable.class));
    }
}
