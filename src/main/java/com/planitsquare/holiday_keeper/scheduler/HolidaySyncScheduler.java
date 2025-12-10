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
            final Integer[] targetYears = getTargetYears();
            log.info(LogMessage.SYNC_YEARS.getMessage(), targetYears[0], targetYears[1]);

            final List<String> countryCodes = getAllCountryCodes();
            final Integer totalSynced = syncAllCountries(countryCodes, targetYears);

            log.info(LogMessage.SYNC_ALL_COMPLETED.getMessage(), totalSynced);
        } catch (final Exception e) {
            log.error(LogMessage.SYNC_ERROR.getMessage(), e);
        }
    }

    private Integer[] getTargetYears() {
        final Integer currentYear = LocalDate.now().getYear();
        final Integer previousYear = currentYear - 1;
        return new Integer[] {previousYear, currentYear};
    }

    private List<String> getAllCountryCodes() {
        return countryService.findAll().stream()
                .map(com.planitsquare.holiday_keeper.domain.entity.Country::getCountryCode)
                .toList();
    }

    private Integer syncAllCountries(final List<String> countryCodes, final Integer[] targetYears) {
        Integer totalSynced = 0;
        for (final String countryCode : countryCodes) {
            totalSynced += syncSingleCountry(countryCode, targetYears);
        }
        return totalSynced;
    }

    private Integer syncSingleCountry(final String countryCode, final Integer[] targetYears) {
        try {
            final Integer count1 = syncCountryForYear(targetYears[0], countryCode);
            final Integer count2 = syncCountryForYear(targetYears[1], countryCode);
            return count1 + count2;
        } catch (final Exception e) {
            log.error(LogMessage.SYNC_FAILED.getMessage(), targetYears[0], targetYears[1],
                    countryCode, e.getMessage());
            return 0;
        }
    }

    private Integer syncCountryForYear(final Integer year, final String countryCode) {
        final Integer count = holidayService.refreshHolidays(year, countryCode);
        log.debug(LogMessage.SYNC_COMPLETED.getMessage(), year, countryCode, count);
        return count;
    }
}
