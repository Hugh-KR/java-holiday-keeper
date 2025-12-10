package com.planitsquare.holiday_keeper.exception;

import static com.planitsquare.holiday_keeper.constants.ErrorMessage.COUNTRY_NOT_FOUND;

public class CountryNotFoundException extends RuntimeException {

    private final String countryCode;

    public CountryNotFoundException(final String countryCode) {
        super(COUNTRY_NOT_FOUND.getMessage().formatted(countryCode));
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
