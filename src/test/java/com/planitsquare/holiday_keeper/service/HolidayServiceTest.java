package com.planitsquare.holiday_keeper.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.planitsquare.holiday_keeper.domain.entity.Country;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;
import com.planitsquare.holiday_keeper.domain.repository.HolidayRepository;
import com.planitsquare.holiday_keeper.external.client.NagerDateClient;
import com.planitsquare.holiday_keeper.external.dto.NagerHolidayResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("HolidayDataService 테스트")
class HolidayDataServiceTest {

    @Mock
    private NagerDateClient nagerDateClient;

    @Mock
    private CountryService countryService;

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayDataService holidayDataService;

    private Country testCountry;
    private NagerHolidayResponse testHolidayResponse;

    @BeforeEach
    void setUp() {
        testCountry = Country.builder().countryCode("KR").name("South Korea").build();

        testHolidayResponse = new NagerHolidayResponse(LocalDate.of(2024, 1, 1), "New Year's Day",
                "신정", "KR", true, false, null, 1949, Arrays.asList("Public"));

        org.springframework.test.util.ReflectionTestUtils.setField(holidayDataService, "startYear",
                2020);
        org.springframework.test.util.ReflectionTestUtils.setField(holidayDataService, "endYear",
                2025);
    }

    @Test
    @DisplayName("특정 연도/국가 공휴일 적재 성공")
    void loadHolidaysForYearAndCountry_Success() {
        // given
        when(nagerDateClient.getPublicHolidays(2024, "KR"))
                .thenReturn(Collections.singletonList(testHolidayResponse));
        when(holidayRepository.save(any(PublicHoliday.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        final int count = holidayDataService.loadHolidaysForYearAndCountry(2024, "KR", testCountry);

        // then
        assertThat(count).isEqualTo(1);
        verify(holidayRepository).deleteByCountryCodeAndYear("KR", 2024);
        verify(holidayRepository).save(any(PublicHoliday.class));
    }

    @Test
    @DisplayName("공휴일 삭제 성공")
    void deleteHolidays_Success() {
        // given
        doNothing().when(countryService).validateCountryExists("KR");

        // when
        holidayDataService.deleteHolidays(2024, "KR");

        // then
        verify(countryService).validateCountryExists("KR");
        verify(holidayRepository).deleteByCountryCodeAndYear("KR", 2024);
    }

    @Test
    @DisplayName("공휴일 삭제 실패 - 국가 코드 없음")
    void deleteHolidays_Fail_CountryNotFound() {
        // given
        doThrow(new IllegalArgumentException("국가 코드 'XX'를 찾을 수 없습니다")).when(countryService)
                .validateCountryExists("XX");

        // when & then
        assertThatThrownBy(() -> holidayDataService.deleteHolidays(2024, "XX"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("국가 코드 'XX'를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("전체 공휴일 적재 성공")
    void loadAllHolidays_Success() {
        // given
        final Country country1 = Country.builder().countryCode("KR").name("South Korea").build();
        final Country country2 = Country.builder().countryCode("US").name("United States").build();
        org.springframework.test.util.ReflectionTestUtils.setField(country1, "id", 1L);
        org.springframework.test.util.ReflectionTestUtils.setField(country2, "id", 2L);
        final List<Country> countries = Arrays.asList(country1, country2);

        when(countryService.fetchAndSaveAllCountries()).thenReturn(countries);
        when(nagerDateClient.getPublicHolidays(any(Integer.class), any(String.class)))
                .thenReturn(Collections.singletonList(testHolidayResponse));
        when(holidayRepository.save(any(PublicHoliday.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        final Integer result = holidayDataService.loadAllHolidays();

        // then
        assertThat(result).isGreaterThan(0);
        verify(countryService).fetchAndSaveAllCountries();
        verify(holidayRepository, atLeastOnce()).save(any(PublicHoliday.class));
    }

    @Test
    @DisplayName("특정 연도/국가 공휴일 적재 성공 - 빈 리스트 반환")
    void loadHolidaysForYearAndCountry_Success_EmptyList() {
        // given
        when(nagerDateClient.getPublicHolidays(2024, "KR")).thenReturn(Collections.emptyList());

        // when
        final int count = holidayDataService.loadHolidaysForYearAndCountry(2024, "KR", testCountry);

        // then
        assertThat(count).isZero();
        verify(nagerDateClient).getPublicHolidays(2024, "KR");
        verify(holidayRepository, never()).deleteByCountryCodeAndYear(any(), any());
        verify(holidayRepository, never()).save(any(PublicHoliday.class));
    }

    @Test
    @DisplayName("특정 연도/국가 공휴일 적재 실패 - 외부 API 예외")
    void loadHolidaysForYearAndCountry_Fail_ExternalApiException() {
        // given
        when(nagerDateClient.getPublicHolidays(2024, "KR"))
                .thenThrow(new RuntimeException("외부 API 호출 실패"));

        // when & then
        assertThatThrownBy(
                () -> holidayDataService.loadHolidaysForYearAndCountry(2024, "KR", testCountry))
                        .isInstanceOf(RuntimeException.class).hasMessageContaining("외부 API 호출 실패");
        verify(nagerDateClient).getPublicHolidays(2024, "KR");
        verify(holidayRepository, never()).deleteByCountryCodeAndYear(any(), any());
    }
}
