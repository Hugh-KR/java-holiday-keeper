package com.planitsquare.holiday_keeper.service;

import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_ALL_COMPLETED;
import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_ALL_START;
import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_HOLIDAYS_COMPLETED;
import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_HOLIDAYS_EMPTY;
import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_HOLIDAYS_FAILED;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planitsquare.holiday_keeper.constants.HolidayType;
import com.planitsquare.holiday_keeper.domain.entity.Country;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;
import com.planitsquare.holiday_keeper.domain.repository.HolidayRepository;
import com.planitsquare.holiday_keeper.external.client.NagerDateClient;
import com.planitsquare.holiday_keeper.external.dto.NagerHolidayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HolidayDataService {

    private final NagerDateClient nagerDateClient;
    private final HolidayRepository holidayRepository;
    private final CountryService countryService;

    @Value("${holiday.data.start-year}")
    private Integer startYear;

    @Value("${holiday.data.end-year}")
    private Integer endYear;

    @Transactional
    public Integer loadAllHolidays() {
        log.info(LOAD_ALL_START.getMessage(), startYear, endYear);

        final List<Country> countries = countryService.fetchAndSaveAllCountries();
        final Map<String, Country> countryMap = buildCountryMap(countries);
        final Integer totalLoaded = loadHolidaysForAllCountriesAndYears(countries, countryMap);

        log.info(LOAD_ALL_COMPLETED.getMessage(), totalLoaded);
        return totalLoaded;
    }

    @Transactional
    public Integer loadHolidaysForYearAndCountry(final Integer year, final String countryCode,
            final Country country) {
        final List<NagerHolidayResponse> holidays =
                nagerDateClient.getPublicHolidays(year, countryCode);

        if (holidays.isEmpty()) {
            log.warn(LOAD_HOLIDAYS_EMPTY.getMessage(), year, countryCode);
            return 0;
        }

        deleteExistingHolidays(countryCode, year);
        final Integer savedCount = saveHolidays(holidays, year, countryCode, country);

        log.info(LOAD_HOLIDAYS_COMPLETED.getMessage(), year, countryCode, savedCount);
        return savedCount;
    }

    @Transactional
    public void deleteHolidays(final Integer year, final String countryCode) {
        countryService.validateCountryExists(countryCode);
        deleteExistingHolidays(countryCode, year);
    }

    private Map<String, Country> buildCountryMap(final List<Country> countries) {
        return countries.stream()
                .collect(Collectors.toMap(Country::getCountryCode, country -> country));
    }

    private Integer loadHolidaysForAllCountriesAndYears(final List<Country> countries,
            final Map<String, Country> countryMap) {
        Integer totalLoaded = 0;
        for (final Country country : countries) {
            totalLoaded += loadHolidaysForCountry(country, countryMap);
        }
        return totalLoaded;
    }

    private Integer loadHolidaysForCountry(final Country country,
            final Map<String, Country> countryMap) {
        final String countryCode = country.getCountryCode();
        Integer loadedCount = 0;

        for (Integer year = startYear; year <= endYear; year++) {
            try {
                final Integer loaded = loadHolidaysForYearAndCountry(year, countryCode,
                        countryMap.get(countryCode));
                loadedCount += loaded;
                log.debug(LOAD_HOLIDAYS_COMPLETED.getMessage(), year, countryCode, loaded);
            } catch (final Exception e) {
                log.error(LOAD_HOLIDAYS_FAILED.getMessage(), year, countryCode, e.getMessage());
            }
        }
        return loadedCount;
    }

    private void deleteExistingHolidays(final String countryCode, final Integer year) {
        holidayRepository.deleteByCountryCodeAndYear(countryCode, year);
    }

    private Integer saveHolidays(final List<NagerHolidayResponse> holidays, final Integer year,
            final String countryCode, final Country country) {
        Integer savedCount = 0;
        for (final NagerHolidayResponse holidayResponse : holidays) {
            final PublicHoliday holiday =
                    convertToEntity(holidayResponse, year, countryCode, country);
            holidayRepository.save(holiday);
            savedCount++;
        }
        return savedCount;
    }

    private PublicHoliday convertToEntity(final NagerHolidayResponse response, final Integer year,
            final String countryCode, final Country country) {
        return PublicHoliday.builder().country(country).year(year).date(response.date())
                .name(response.name()).localName(response.localName()).countryCode(countryCode)
                .types(extractTypes(response)).fixed(extractFixed(response))
                .global(extractGlobal(response)).launchYear(extractLaunchYear(response)).build();
    }

    private String extractTypes(final NagerHolidayResponse response) {
        return response.types() != null ? String.join(",", response.types())
                : HolidayType.getDefaultValue();
    }

    private Boolean extractFixed(final NagerHolidayResponse response) {
        return extractBooleanOrDefault(response.fixed());
    }

    private Boolean extractGlobal(final NagerHolidayResponse response) {
        return extractBooleanOrDefault(response.global());
    }

    private Boolean extractBooleanOrDefault(final Boolean value) {
        return value != null ? value : Boolean.FALSE;
    }

    private String extractLaunchYear(final NagerHolidayResponse response) {
        return response.launchYear() != null ? String.valueOf(response.launchYear()) : null;
    }
}
