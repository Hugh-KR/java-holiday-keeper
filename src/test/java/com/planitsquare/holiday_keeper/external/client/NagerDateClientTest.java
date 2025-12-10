package com.planitsquare.holiday_keeper.external.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.planitsquare.holiday_keeper.external.dto.NagerCountryResponse;
import com.planitsquare.holiday_keeper.external.dto.NagerHolidayResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("NagerDateClient 테스트")
class NagerDateClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NagerDateClient nagerDateClient;

    private static final String BASE_URL = "https://date.nager.at/api/v3";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(nagerDateClient, "baseUrl", BASE_URL);
    }

    @Test
    @DisplayName("사용 가능한 국가 목록 조회 성공")
    void getAvailableCountries_Success() {
        // given
        final NagerCountryResponse country1 = new NagerCountryResponse("KR", "South Korea");
        final NagerCountryResponse country2 = new NagerCountryResponse("US", "United States");
        final List<NagerCountryResponse> countries = Arrays.asList(country1, country2);
        final ResponseEntity<List<NagerCountryResponse>> responseEntity =
                new ResponseEntity<>(countries, HttpStatus.OK);

        when(restTemplate.exchange(eq(BASE_URL + "/AvailableCountries"), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        // when
        final List<NagerCountryResponse> result = nagerDateClient.getAvailableCountries();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).countryCode()).isEqualTo("KR");
        assertThat(result.get(1).countryCode()).isEqualTo("US");
    }

    @Test
    @DisplayName("사용 가능한 국가 목록 조회 실패 - RestClientException")
    void getAvailableCountries_Fail_RestClientException() {
        // given
        when(restTemplate.exchange(eq(BASE_URL + "/AvailableCountries"), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class)))
                        .thenThrow(new RestClientException("연결 실패"));

        // when & then
        assertThatThrownBy(() -> nagerDateClient.getAvailableCountries())
                .isInstanceOf(RuntimeException.class).hasMessageContaining("연결 실패");
    }

    @Test
    @DisplayName("사용 가능한 국가 목록 조회 성공 - 빈 리스트")
    void getAvailableCountries_Success_EmptyList() {
        // given
        final ResponseEntity<List<NagerCountryResponse>> responseEntity =
                new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);

        when(restTemplate.exchange(eq(BASE_URL + "/AvailableCountries"), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        // when
        final List<NagerCountryResponse> result = nagerDateClient.getAvailableCountries();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("공휴일 목록 조회 성공")
    void getPublicHolidays_Success() {
        // given
        final NagerHolidayResponse holiday = new NagerHolidayResponse(LocalDate.of(2024, 1, 1),
                "신정", "New Year's Day", "KR", true, false, null, 1949,
                Arrays.asList("Public"));
        final List<NagerHolidayResponse> holidays = Collections.singletonList(holiday);
        final ResponseEntity<List<NagerHolidayResponse>> responseEntity =
                new ResponseEntity<>(holidays, HttpStatus.OK);

        when(restTemplate.exchange(eq(BASE_URL + "/PublicHolidays/2024/KR"), eq(HttpMethod.GET),
                eq(null), any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        // when
        final List<NagerHolidayResponse> result = nagerDateClient.getPublicHolidays(2024, "KR");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).date()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(result.get(0).name()).isEqualTo("New Year's Day");
        assertThat(result.get(0).localName()).isEqualTo("신정");
    }

    @Test
    @DisplayName("공휴일 목록 조회 실패 - RestClientException")
    void getPublicHolidays_Fail_RestClientException() {
        // given
        when(restTemplate.exchange(eq(BASE_URL + "/PublicHolidays/2024/KR"), eq(HttpMethod.GET),
                eq(null), any(ParameterizedTypeReference.class)))
                        .thenThrow(new RestClientException("연결 실패"));

        // when & then
        assertThatThrownBy(() -> nagerDateClient.getPublicHolidays(2024, "KR"))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("연결 실패");
    }

    @Test
    @DisplayName("공휴일 목록 조회 성공 - null 응답 처리")
    void getPublicHolidays_Success_NullResponse() {
        // given
        final ResponseEntity<List<NagerHolidayResponse>> responseEntity =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(eq(BASE_URL + "/PublicHolidays/2024/KR"), eq(HttpMethod.GET),
                eq(null), any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        // when
        final List<NagerHolidayResponse> result = nagerDateClient.getPublicHolidays(2024, "KR");

        // then
        assertThat(result).isEmpty();
    }
}
