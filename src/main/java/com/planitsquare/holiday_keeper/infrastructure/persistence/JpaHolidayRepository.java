package com.planitsquare.holiday_keeper.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;

interface JpaHolidayRepository extends JpaRepository<PublicHoliday, Long> {

    List<PublicHoliday> findByCountryCodeAndYear(String countryCode, Integer year);

    Boolean existsByCountryCodeAndYear(String countryCode, Integer year);

    @Modifying
    @Query("DELETE FROM PublicHoliday ph WHERE ph.countryCode = :countryCode AND ph.year = :year")
    void deleteByCountryCodeAndYear(@Param("countryCode") String countryCode,
            @Param("year") Integer year);
}
