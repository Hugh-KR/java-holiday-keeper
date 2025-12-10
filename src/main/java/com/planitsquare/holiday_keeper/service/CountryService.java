package com.planitsquare.holiday_keeper.service;

import static com.planitsquare.holiday_keeper.constants.LogMessage.COUNTRIES_FOUND;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

        return saveCountries(countryResponses);
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

    public List<String> findAllCountryCodes() {
        return countryRepository.findAllCountryCodes();
    }

    @Transactional
    private List<Country> saveCountries(final List<NagerCountryResponse> countryResponses) {
        final Map<String, Country> existingCountriesMap =
                buildExistingCountriesMap(countryResponses);
        final List<Country> processedCountries =
                processAllCountries(countryResponses, existingCountriesMap);
        saveNewCountries(processedCountries);

        return processedCountries;
    }

    private Map<String, Country> buildExistingCountriesMap(
            final List<NagerCountryResponse> countryResponses) {
        final List<String> countryCodes =
                countryResponses.stream().map(NagerCountryResponse::countryCode).toList();
        return countryRepository.findAllByCountryCodeIn(countryCodes).stream()
                .collect(Collectors.toMap(Country::getCountryCode, country -> country));
    }

    private List<Country> processAllCountries(final List<NagerCountryResponse> countryResponses,
            final Map<String, Country> existingCountriesMap) {
        final List<Country> allCountries = new ArrayList<>();

        for (final NagerCountryResponse response : countryResponses) {
            final Country country = processCountry(response, existingCountriesMap);
            allCountries.add(country);
        }

        return allCountries;
    }

    private Country processCountry(final NagerCountryResponse response,
            final Map<String, Country> existingCountriesMap) {
        final Country country = existingCountriesMap.getOrDefault(response.countryCode(),
                createNewCountry(response));

        if (isNewCountry(country)) {
            return country;
        } else {
            country.updateName(response.name());
            return country;
        }
    }

    private void saveNewCountries(final List<Country> allCountries) {
        final List<Country> countriesToSave =
                allCountries.stream().filter(this::isNewCountry).toList();

        if (!countriesToSave.isEmpty()) {
            countryRepository.saveAll(countriesToSave);
        }
    }

    private Country createNewCountry(final NagerCountryResponse response) {
        return Country.builder().countryCode(response.countryCode()).name(response.name()).build();
    }

    private boolean isNewCountry(final Country country) {
        return country.getId() == null;
    }
}
