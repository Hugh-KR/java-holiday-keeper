package com.planitsquare.holiday_keeper.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.planitsquare.holiday_keeper.domain.entity.Country;
import com.planitsquare.holiday_keeper.service.CountryService;
import com.planitsquare.holiday_keeper.service.HolidayService;

@ExtendWith(MockitoExtension.class)
@DisplayName("HolidaySyncScheduler 테스트")
class HolidaySyncSchedulerTest {

    @Mock
    private HolidayService holidayService;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private HolidaySyncScheduler holidaySyncScheduler;

    private Country testCountry1;
    private Country testCountry2;

    @BeforeEach
    void setUp() {
        testCountry1 = Country.builder().countryCode("KR").name("South Korea").build();
        testCountry2 = Country.builder().countryCode("US").name("United States").build();
        org.springframework.test.util.ReflectionTestUtils.setField(testCountry1, "id", 1L);
        org.springframework.test.util.ReflectionTestUtils.setField(testCountry2, "id", 2L);
    }

    @Test
    @DisplayName("공휴일 동기화 성공 - 여러 국가")
    void syncHolidays_Success_MultipleCountries() {
        // given
        final int currentYear = java.time.LocalDate.now().getYear();
        final int previousYear = currentYear - 1;
        final java.util.List<Country> countries = Arrays.asList(testCountry1, testCountry2);

        when(countryService.findAll()).thenReturn(countries);
        when(holidayService.refreshHolidays(eq(previousYear), eq("KR"))).thenReturn(10);
        when(holidayService.refreshHolidays(eq(currentYear), eq("KR"))).thenReturn(10);
        when(holidayService.refreshHolidays(eq(previousYear), eq("US"))).thenReturn(15);
        when(holidayService.refreshHolidays(eq(currentYear), eq("US"))).thenReturn(15);

        // when
        holidaySyncScheduler.syncHolidays();

        // then
        verify(countryService).findAll();
        verify(holidayService).refreshHolidays(previousYear, "KR");
        verify(holidayService).refreshHolidays(currentYear, "KR");
        verify(holidayService).refreshHolidays(previousYear, "US");
        verify(holidayService).refreshHolidays(currentYear, "US");
    }

    @Test
    @DisplayName("공휴일 동기화 성공 - 빈 국가 목록")
    void syncHolidays_Success_EmptyCountries() {
        // given
        when(countryService.findAll()).thenReturn(Collections.emptyList());

        // when
        holidaySyncScheduler.syncHolidays();

        // then
        verify(countryService).findAll();
        verify(holidayService, never()).refreshHolidays(any(Integer.class), any(String.class));
    }

    @Test
    @DisplayName("공휴일 동기화 실패 - 예외 발생 시 해당 국가만 스킵하고 계속 진행")
    void syncHolidays_Fail_ExceptionHandling() {
        // given
        final int currentYear = java.time.LocalDate.now().getYear();
        final int previousYear = currentYear - 1;
        final java.util.List<Country> countries = Arrays.asList(testCountry1, testCountry2);

        when(countryService.findAll()).thenReturn(countries);
        when(holidayService.refreshHolidays(eq(previousYear), eq("KR")))
                .thenThrow(new RuntimeException("동기화 실패"));
        when(holidayService.refreshHolidays(eq(previousYear), eq("US"))).thenReturn(15);
        when(holidayService.refreshHolidays(eq(currentYear), eq("US"))).thenReturn(15);

        // when
        holidaySyncScheduler.syncHolidays();

        // then
        verify(countryService).findAll();
        verify(holidayService).refreshHolidays(previousYear, "KR");
        verify(holidayService, never()).refreshHolidays(currentYear, "KR");
        verify(holidayService).refreshHolidays(previousYear, "US");
        verify(holidayService).refreshHolidays(currentYear, "US");
    }
}
