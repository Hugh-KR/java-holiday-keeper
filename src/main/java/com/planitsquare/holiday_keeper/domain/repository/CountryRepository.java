package com.planitsquare.holiday_keeper.domain.repository;

import java.util.List;
import java.util.Optional;
import com.planitsquare.holiday_keeper.domain.entity.Country;

public interface CountryRepository {

    Country save(Country country);

    List<Country> findAll();

    Optional<Country> findByCountryCode(String countryCode);

    Boolean existsByCountryCode(String countryCode);
}
