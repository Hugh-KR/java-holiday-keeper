package com.planitsquare.holiday_keeper.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.planitsquare.holiday_keeper.domain.entity.Country;

interface JpaCountryRepository extends JpaRepository<Country, Long> {

    Boolean existsByCountryCode(String countryCode);

    Optional<Country> findByCountryCode(String countryCode);

    List<Country> findAllByCountryCodeIn(List<String> countryCodes);

    @Query("SELECT c.countryCode FROM Country c")
    List<String> findAllCountryCodes();
}
