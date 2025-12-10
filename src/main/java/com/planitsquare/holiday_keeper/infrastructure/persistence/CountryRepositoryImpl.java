package com.planitsquare.holiday_keeper.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.planitsquare.holiday_keeper.domain.entity.Country;
import com.planitsquare.holiday_keeper.domain.repository.CountryRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CountryRepositoryImpl implements CountryRepository {

    private final JpaCountryRepository jpaRepository;

    @Override
    public Country save(final Country country) {
        return jpaRepository.save(country);
    }

    @Override
    public List<Country> saveAll(final List<Country> countries) {
        return jpaRepository.saveAll(countries);
    }

    @Override
    public List<Country> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Country> findAllByCountryCodeIn(final List<String> countryCodes) {
        return jpaRepository.findAllByCountryCodeIn(countryCodes);
    }

    @Override
    public List<String> findAllCountryCodes() {
        return jpaRepository.findAllCountryCodes();
    }

    @Override
    public Optional<Country> findByCountryCode(final String countryCode) {
        return jpaRepository.findByCountryCode(countryCode);
    }

    @Override
    public Boolean existsByCountryCode(final String countryCode) {
        return jpaRepository.existsByCountryCode(countryCode);
    }
}
