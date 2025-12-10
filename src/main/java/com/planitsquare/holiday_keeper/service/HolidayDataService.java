package com.planitsquare.holiday_keeper.service;

import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_ALL_COMPLETED;
import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_ALL_START;
import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_HOLIDAYS_COMPLETED;
import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_HOLIDAYS_EMPTY;
import static com.planitsquare.holiday_keeper.constants.LogMessage.LOAD_HOLIDAYS_FAILED;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        final Integer totalLoaded = loadHolidaysForAllCountriesAndYears(countries);

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

        final Integer savedCount = upsertHolidays(holidays, year, countryCode, country);

        log.info(LOAD_HOLIDAYS_COMPLETED.getMessage(), year, countryCode, savedCount);
        return savedCount;
    }

    @Transactional
    public void deleteHolidays(final Integer year, final String countryCode) {
        countryService.validateCountryExists(countryCode);
        deleteExistingHolidays(countryCode, year);
    }

    private Integer loadHolidaysForAllCountriesAndYears(final List<Country> countries) {
        return countries.stream().mapToInt(this::loadHolidaysForCountry).sum();
    }

    private Integer loadHolidaysForCountry(final Country country) {
        final String countryCode = country.getCountryCode();
        return IntStream.rangeClosed(startYear, endYear)
                .map(year -> loadHolidaysForSingleYear(year, countryCode, country)).sum();
    }

    private Integer loadHolidaysForSingleYear(final Integer year, final String countryCode,
            final Country country) {
        try {
            final Integer loaded = loadHolidaysForYearAndCountry(year, countryCode, country);
            log.debug(LOAD_HOLIDAYS_COMPLETED.getMessage(), year, countryCode, loaded);
            return loaded;
        } catch (final RuntimeException e) {
            log.error(LOAD_HOLIDAYS_FAILED.getMessage(), year, countryCode, e.getMessage());
            return 0;
        }
    }

    private void deleteExistingHolidays(final String countryCode, final Integer year) {
        holidayRepository.deleteByCountryCodeAndYear(countryCode, year);
    }

    private Integer upsertHolidays(final List<NagerHolidayResponse> holidays, final Integer year,
            final String countryCode, final Country country) {
        final Map<LocalDate, PublicHoliday> existingHolidaysMap =
                buildExistingHolidaysMap(countryCode, year);
        final Set<LocalDate> newHolidayDates = extractHolidayDates(holidays);
        final List<PublicHoliday> holidaysToSave =
                processHolidays(holidays, year, countryCode, country, existingHolidaysMap);

        deleteRemovedHolidays(existingHolidaysMap, newHolidayDates);

        if (!holidaysToSave.isEmpty()) {
            holidayRepository.saveAll(holidaysToSave);
        }

        return holidaysToSave.size();
    }

    private Map<LocalDate, PublicHoliday> buildExistingHolidaysMap(final String countryCode,
            final Integer year) {
        return holidayRepository.findByCountryCodeAndYear(countryCode, year).stream()
                .collect(Collectors.toMap(PublicHoliday::getDate, holiday -> holiday));
    }

    private Set<LocalDate> extractHolidayDates(final List<NagerHolidayResponse> holidays) {
        return holidays.stream().map(NagerHolidayResponse::date).collect(Collectors.toSet());
    }

    private void deleteRemovedHolidays(final Map<LocalDate, PublicHoliday> existingHolidaysMap,
            final Set<LocalDate> newHolidayDates) {
        final List<PublicHoliday> holidaysToDelete = existingHolidaysMap.values().stream()
                .filter(holiday -> !newHolidayDates.contains(holiday.getDate())).toList();

        if (!holidaysToDelete.isEmpty()) {
            holidayRepository.deleteAll(holidaysToDelete);
        }
    }

    private List<PublicHoliday> processHolidays(final List<NagerHolidayResponse> holidays,
            final Integer year, final String countryCode, final Country country,
            final Map<LocalDate, PublicHoliday> existingHolidaysMap) {
        final List<PublicHoliday> holidaysToSave = new ArrayList<>();

        for (final NagerHolidayResponse response : holidays) {
            final PublicHoliday existingHoliday = existingHolidaysMap.get(response.date());

            if (existingHoliday != null) {
                final PublicHoliday updatedHoliday =
                        convertToEntity(response, year, countryCode, country);
                existingHoliday.updateFrom(updatedHoliday);
                holidaysToSave.add(existingHoliday);
            } else {
                final PublicHoliday newHoliday =
                        convertToEntity(response, year, countryCode, country);
                holidaysToSave.add(newHoliday);
            }
        }

        return holidaysToSave;
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

    private boolean extractFixed(final NagerHolidayResponse response) {
        return extractBooleanOrDefault(response.fixed());
    }

    private boolean extractGlobal(final NagerHolidayResponse response) {
        return extractBooleanOrDefault(response.global());
    }

    private boolean extractBooleanOrDefault(final Boolean value) {
        return value != null ? value : false;
    }

    private String extractLaunchYear(final NagerHolidayResponse response) {
        return response.launchYear() != null ? String.valueOf(response.launchYear()) : null;
    }
}
