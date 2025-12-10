package com.planitsquare.holiday_keeper.external.client;

import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_COUNTRIES_REQUEST;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_COUNTRIES_SUCCESS;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_HOLIDAYS_REQUEST;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_HOLIDAYS_SUCCESS;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_OPERATION_COUNTRIES;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_OPERATION_HOLIDAYS;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_RETRY;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_RETRY_SKIPPED;
import static com.planitsquare.holiday_keeper.constants.LogMessage.EXTERNAL_API_RETRY_STATUS;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.planitsquare.holiday_keeper.external.dto.NagerCountryResponse;
import com.planitsquare.holiday_keeper.external.dto.NagerHolidayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NagerDateClient {

    private final RestTemplate restTemplate;

    @Value("${external.api.nager-date.base-url}")
    private String baseUrl;

    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public List<NagerCountryResponse> getAvailableCountries() {
        final String url = buildCountriesUrl();
        log.debug(EXTERNAL_API_COUNTRIES_REQUEST.getMessage(), url);

        try {
            final List<NagerCountryResponse> countries = fetchCountries(url);
            log.info(EXTERNAL_API_COUNTRIES_SUCCESS.getMessage(), countries.size());
            return countries;
        } catch (final RestClientException e) {
            log.warn(EXTERNAL_API_RETRY.getMessage(), EXTERNAL_API_OPERATION_COUNTRIES.getMessage(),
                    EXTERNAL_API_RETRY_STATUS.getMessage());
            throw e;
        }
    }

    @Recover
    public List<NagerCountryResponse> recoverCountries(final RestClientException e) {
        log.error(EXTERNAL_API_RETRY_SKIPPED.getMessage(),
                EXTERNAL_API_OPERATION_COUNTRIES.getMessage());
        return Collections.emptyList();
    }

    private String buildCountriesUrl() {
        return baseUrl + "/AvailableCountries";
    }

    private List<NagerCountryResponse> fetchCountries(final String url) {
        final ResponseEntity<List<NagerCountryResponse>> response =
                restTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<NagerCountryResponse>>() {});
        return extractResponseBody(response);
    }


    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public List<NagerHolidayResponse> getPublicHolidays(final Integer year,
            final String countryCode) {
        final String url = buildHolidaysUrl(year, countryCode);
        log.debug(EXTERNAL_API_HOLIDAYS_REQUEST.getMessage(), url);

        try {
            final List<NagerHolidayResponse> holidays = fetchHolidays(url);
            log.info(EXTERNAL_API_HOLIDAYS_SUCCESS.getMessage(), holidays.size(), countryCode,
                    year);
            return holidays;
        } catch (final RestClientException e) {
            final String operation = "%s (%s/%d)"
                    .formatted(EXTERNAL_API_OPERATION_HOLIDAYS.getMessage(), countryCode, year);
            log.warn(EXTERNAL_API_RETRY.getMessage(), operation,
                    EXTERNAL_API_RETRY_STATUS.getMessage());
            throw e;
        }
    }

    @Recover
    public List<NagerHolidayResponse> recoverHolidays(final RestClientException e,
            final Integer year, final String countryCode) {
        final String operation = "%s (%s/%d)"
                .formatted(EXTERNAL_API_OPERATION_HOLIDAYS.getMessage(), countryCode, year);
        log.error(EXTERNAL_API_RETRY_SKIPPED.getMessage(), operation);
        return Collections.emptyList();
    }

    private String buildHolidaysUrl(final Integer year, final String countryCode) {
        return "%s/PublicHolidays/%d/%s".formatted(baseUrl, year, countryCode);
    }

    private List<NagerHolidayResponse> fetchHolidays(final String url) {
        final ResponseEntity<List<NagerHolidayResponse>> response =
                restTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<NagerHolidayResponse>>() {});
        return extractResponseBody(response);
    }

    private <T> List<T> extractResponseBody(final ResponseEntity<List<T>> response) {
        final List<T> body = response.getBody();
        return body != null ? body : Collections.emptyList();
    }

}
