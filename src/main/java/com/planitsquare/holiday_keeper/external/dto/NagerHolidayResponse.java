package com.planitsquare.holiday_keeper.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record NagerHolidayResponse(
        @JsonProperty("date") LocalDate date,
        @JsonProperty("localName") String localName,
        @JsonProperty("name") String name,
        @JsonProperty("countryCode") String countryCode,
        @JsonProperty("fixed") Boolean fixed,
        @JsonProperty("global") Boolean global,
        @JsonProperty("counties") List<String> counties,
        @JsonProperty("launchYear") Integer launchYear,
        @JsonProperty("types") List<String> types) {}