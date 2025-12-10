package com.planitsquare.holiday_keeper.scheduler;

import java.time.LocalDate;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.planitsquare.holiday_keeper.constants.LogMessage;
import com.planitsquare.holiday_keeper.service.CountryService;
import com.planitsquare.holiday_keeper.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidaySyncScheduler {

    private final HolidayService holidayService;
    private final CountryService countryService;

    @Scheduled(cron = "${batch.sync.cron}")
    public void syncHolidays() {
        log.info(LogMessage.SYNC_START.getMessage());

        try {
            final Integer currentYear = LocalDate.now().getYear();
            final Integer previousYear = currentYear - 1;
            log.info(LogMessage.SYNC_YEARS.getMessage(), previousYear, currentYear);

            final List<String> countryCodes = getAllCountryCodes();
            final Integer totalSynced = syncAllCountries(countryCodes, previousYear, currentYear);

            log.info(LogMessage.SYNC_ALL_COMPLETED.getMessage(), totalSynced);
        } catch (final RuntimeException e) {
            log.error(LogMessage.SYNC_ERROR.getMessage(), e);
        }
    }

    private List<String> getAllCountryCodes() {
        return countryService.findAllCountryCodes();
    }

    private Integer syncAllCountries(final List<String> countryCodes, final Integer previousYear,
            final Integer currentYear) {
        return countryCodes.stream()
                .mapToInt(countryCode -> syncSingleCountry(countryCode, previousYear, currentYear))
                .sum();
    }

    private Integer syncSingleCountry(final String countryCode, final Integer previousYear,
            final Integer currentYear) {
        try {
            final Integer count1 = syncCountryForYear(previousYear, countryCode);
            final Integer count2 = syncCountryForYear(currentYear, countryCode);
            return count1 + count2;
        } catch (final RuntimeException e) {
            log.error(LogMessage.SYNC_FAILED.getMessage(), previousYear, currentYear, countryCode,
                    e.getMessage());
            return 0;
        }
    }

    private Integer syncCountryForYear(final Integer year, final String countryCode) {
        final Integer count = holidayService.refreshHolidays(year, countryCode);
        log.debug(LogMessage.SYNC_COMPLETED.getMessage(), year, countryCode, count);
        return count;
    }
}
