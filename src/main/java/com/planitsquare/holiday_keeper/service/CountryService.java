package com.planitsquare.holiday_keeper.service;

import static com.planitsquare.holiday_keeper.constants.LogMessage.COUNTRIES_FOUND;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planitsquare.holiday_keeper.domain.entity.Country;
import com.planitsquare.holiday_keeper.domain.repository.CountryRepository;
import com.planitsquare.holiday_keeper.exception.CountryNotFoundException;
import com.planitsquare.holiday_keeper.external.client.NagerDateClient;
import com.planitsquare.holiday_keeper.external.dto.NagerCountryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {

    private final NagerDateClient nagerDateClient;
    private final CountryRepository countryRepository;

    @Transactional
    public List<Country> fetchAndSaveAllCountries() {
        final List<NagerCountryResponse> countryResponses = nagerDateClient.getAvailableCountries();
        log.info(COUNTRIES_FOUND.getMessage(), countryResponses.size());

        saveCountries(countryResponses);

        return countryRepository.findAll();
    }

    public Country findByCountryCode(final String countryCode) {
        return countryRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new CountryNotFoundException(countryCode));
    }

    public void validateCountryExists(final String countryCode) {
        if (!countryRepository.existsByCountryCode(countryCode)) {
            throw new CountryNotFoundException(countryCode);
        }
    }

    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    @Transactional
    private void saveCountries(final List<NagerCountryResponse> countryResponses) {
        countryResponses.forEach(this::saveOrUpdateCountry);
    }

    private void saveOrUpdateCountry(final NagerCountryResponse response) {
        final Country country = countryRepository.findByCountryCode(response.countryCode())
                .orElse(createNewCountry(response));

        if (isNewCountry(country)) {
            countryRepository.save(country);
        } else {
            country.updateName(response.name());
        }
    }

    private Country createNewCountry(final NagerCountryResponse response) {
        return Country.builder().countryCode(response.countryCode()).name(response.name()).build();
    }

    private Boolean isNewCountry(final Country country) {
        return country.getId() == null;
    }
}
