package com.planitsquare.holiday_keeper.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NagerCountryResponse(
        @JsonProperty("countryCode") String countryCode, @JsonProperty("name") String name) {}