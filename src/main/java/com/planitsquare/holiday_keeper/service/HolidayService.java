package com.planitsquare.holiday_keeper.service;

import static com.planitsquare.holiday_keeper.constants.LogMessage.REFRESH_START;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planitsquare.holiday_keeper.api.dto.request.HolidaySearchRequest;
import com.planitsquare.holiday_keeper.api.dto.response.HolidayResponse;
import com.planitsquare.holiday_keeper.api.dto.response.PageResponse;
import com.planitsquare.holiday_keeper.domain.entity.Country;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;
import com.planitsquare.holiday_keeper.domain.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final HolidayDataService holidayDataService;
    private final CountryService countryService;

    @Transactional
    public void loadAllHolidays() {
        holidayDataService.loadAllHolidays();
    }

    @Transactional
    public Integer refreshHolidays(final Integer year, final String countryCode) {
        log.info(REFRESH_START.getMessage(), year, countryCode);
        final Country country = countryService.findByCountryCode(countryCode);
        return holidayDataService.loadHolidaysForYearAndCountry(year, countryCode, country);
    }

    @Transactional
    public void deleteHolidays(final Integer year, final String countryCode) {
        holidayDataService.deleteHolidays(year, countryCode);
    }

    public PageResponse<HolidayResponse> searchHolidays(final HolidaySearchRequest request) {
        final HolidaySearchRequest requestWithDefaults = request.withDefaults();
        final Pageable pageable = createPageable(requestWithDefaults);
        final Page<PublicHoliday> holidayPage =
                holidayRepository.search(requestWithDefaults, pageable);
        return convertToPageResponse(holidayPage);
    }

    private Pageable createPageable(final HolidaySearchRequest request) {
        return PageRequest.of(request.page(), request.size());
    }

    private PageResponse<HolidayResponse> convertToPageResponse(
            final Page<PublicHoliday> holidayPage) {
        final List<HolidayResponse> content = convertToHolidayResponses(holidayPage.getContent());
        return PageResponse.of(content, holidayPage.getNumber(), holidayPage.getSize(),
                holidayPage.getTotalElements(), holidayPage.getTotalPages(), holidayPage.isFirst(),
                holidayPage.isLast());
    }

    private List<HolidayResponse> convertToHolidayResponses(final List<PublicHoliday> holidays) {
        return holidays.stream().map(HolidayResponse::from).collect(Collectors.toList());
    }
}
