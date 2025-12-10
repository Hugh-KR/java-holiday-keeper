package com.planitsquare.holiday_keeper.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.planitsquare.holiday_keeper.api.dto.request.HolidaySearchRequest;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;

public interface HolidayRepository {

    PublicHoliday save(PublicHoliday holiday);

    Boolean existsByCountryCodeAndYear(String countryCode, Integer year);

    void deleteByCountryCodeAndYear(String countryCode, Integer year);

    Page<PublicHoliday> search(HolidaySearchRequest request, Pageable pageable);
}
