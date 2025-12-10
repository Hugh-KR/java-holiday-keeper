package com.planitsquare.holiday_keeper.domain.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.planitsquare.holiday_keeper.api.dto.request.HolidaySearchRequest;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;

public interface HolidayRepository {

    PublicHoliday save(PublicHoliday holiday);

    List<PublicHoliday> saveAll(List<PublicHoliday> holidays);

    void deleteAll(List<PublicHoliday> holidays);

    Boolean existsByCountryCodeAndYear(String countryCode, Integer year);

    List<PublicHoliday> findByCountryCodeAndYear(String countryCode, Integer year);

    void deleteByCountryCodeAndYear(String countryCode, Integer year);

    Page<PublicHoliday> search(HolidaySearchRequest request, Pageable pageable);
}
