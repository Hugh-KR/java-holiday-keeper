package com.planitsquare.holiday_keeper.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.planitsquare.holiday_keeper.domain.entity.Country;
import com.planitsquare.holiday_keeper.domain.repository.CountryRepository;
import com.planitsquare.holiday_keeper.external.client.NagerDateClient;
import com.planitsquare.holiday_keeper.external.dto.NagerCountryResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryService 테스트")
class CountryServiceTest {

    @Mock
    private NagerDateClient nagerDateClient;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    private NagerCountryResponse testCountryResponse;
    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountryResponse = new NagerCountryResponse("KR", "South Korea");
        testCountry = Country.builder().countryCode("KR").name("South Korea").build();
        org.springframework.test.util.ReflectionTestUtils.setField(testCountry, "id", 1L);
    }

    @Test
    @DisplayName("모든 국가 조회 및 저장 성공 - 신규 국가")
    void fetchAndSaveAllCountries_Success_NewCountry() {
        // given
        final List<NagerCountryResponse> responses = Collections.singletonList(testCountryResponse);
        when(nagerDateClient.getAvailableCountries()).thenReturn(responses);
        when(countryRepository.findByCountryCode("KR")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> {
            final Country country = invocation.getArgument(0);
            final Country saved = Country.builder().countryCode(country.getCountryCode())
                    .name(country.getName()).build();
            org.springframework.test.util.ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });
        when(countryRepository.findAll()).thenReturn(Collections.singletonList(testCountry));

        // when
        final List<Country> result = countryService.fetchAndSaveAllCountries();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCountryCode()).isEqualTo("KR");
        verify(nagerDateClient).getAvailableCountries();
        verify(countryRepository).save(any(Country.class));
        verify(countryRepository).findAll();
    }

    @Test
    @DisplayName("모든 국가 조회 및 저장 성공 - 기존 국가 업데이트")
    void fetchAndSaveAllCountries_Success_UpdateExisting() {
        // given
        final List<NagerCountryResponse> responses = Collections.singletonList(testCountryResponse);
        final Country existingCountry = Country.builder().countryCode("KR").name("Korea").build();
        org.springframework.test.util.ReflectionTestUtils.setField(existingCountry, "id", 1L);
        when(nagerDateClient.getAvailableCountries()).thenReturn(responses);
        when(countryRepository.findByCountryCode("KR")).thenReturn(Optional.of(existingCountry));
        when(countryRepository.findAll()).thenReturn(Collections.singletonList(testCountry));

        // when
        final List<Country> result = countryService.fetchAndSaveAllCountries();

        // then
        assertThat(result).hasSize(1);
        assertThat(existingCountry.getName()).isEqualTo("South Korea");
        verify(nagerDateClient).getAvailableCountries();
        verify(countryRepository, never()).save(any(Country.class));
        verify(countryRepository).findAll();
    }

    @Test
    @DisplayName("국가 코드로 조회 성공")
    void findByCountryCode_Success() {
        // given
        when(countryRepository.findByCountryCode("KR")).thenReturn(Optional.of(testCountry));

        // when
        final Country result = countryService.findByCountryCode("KR");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCountryCode()).isEqualTo("KR");
        assertThat(result.getName()).isEqualTo("South Korea");
        verify(countryRepository).findByCountryCode("KR");
    }

    @Test
    @DisplayName("국가 코드로 조회 실패 - 국가 없음")
    void findByCountryCode_Fail_NotFound() {
        // given
        when(countryRepository.findByCountryCode("XX")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> countryService.findByCountryCode("XX"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("국가 코드 'XX'를 찾을 수 없습니다");
        verify(countryRepository).findByCountryCode("XX");
    }

    @Test
    @DisplayName("국가 존재 여부 검증 성공")
    void validateCountryExists_Success() {
        // given
        when(countryRepository.existsByCountryCode("KR")).thenReturn(true);

        // when & then
        countryService.validateCountryExists("KR");

        // then
        verify(countryRepository).existsByCountryCode("KR");
    }

    @Test
    @DisplayName("국가 존재 여부 검증 실패 - 국가 없음")
    void validateCountryExists_Fail_NotFound() {
        // given
        when(countryRepository.existsByCountryCode("XX")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> countryService.validateCountryExists("XX"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("국가 코드 'XX'를 찾을 수 없습니다");
        verify(countryRepository).existsByCountryCode("XX");
    }

    @Test
    @DisplayName("모든 국가 조회 성공")
    void findAll_Success() {
        // given
        final Country country1 = Country.builder().countryCode("KR").name("South Korea").build();
        final Country country2 = Country.builder().countryCode("US").name("United States").build();
        org.springframework.test.util.ReflectionTestUtils.setField(country1, "id", 1L);
        org.springframework.test.util.ReflectionTestUtils.setField(country2, "id", 2L);
        final List<Country> countries = Arrays.asList(country1, country2);
        when(countryRepository.findAll()).thenReturn(countries);

        // when
        final List<Country> result = countryService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(country1, country2);
        verify(countryRepository).findAll();
    }
}
