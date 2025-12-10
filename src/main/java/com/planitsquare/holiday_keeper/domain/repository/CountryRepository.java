package com.planitsquare.holiday_keeper.domain.repository;

import java.util.List;
import java.util.Optional;
import com.planitsquare.holiday_keeper.domain.entity.Country;

public interface CountryRepository {

    Country save(Country country);

    List<Country> saveAll(List<Country> countries);

    List<Country> findAll();

    List<Country> findAllByCountryCodeIn(List<String> countryCodes);

    List<String> findAllCountryCodes();

    Optional<Country> findByCountryCode(String countryCode);

    Boolean existsByCountryCode(String countryCode);
}
