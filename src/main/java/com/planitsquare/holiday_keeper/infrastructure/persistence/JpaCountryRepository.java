package com.planitsquare.holiday_keeper.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.planitsquare.holiday_keeper.domain.entity.Country;

interface JpaCountryRepository extends JpaRepository<Country, Long> {

    Boolean existsByCountryCode(String countryCode);

    Optional<Country> findByCountryCode(String countryCode);
}
