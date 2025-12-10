package com.planitsquare.holiday_keeper.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum HolidayType {
    PUBLIC("Public"),

    BANK("Bank"),

    SCHOOL("School"),

    AUTHORITIES("Authorities"),

    OPTIONAL("Optional"),

    OBSERVANCE("Observance");

    private final String value;

    HolidayType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static HolidayType fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return PUBLIC;
        }
        return Arrays.stream(values()).filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst().orElse(PUBLIC);
    }

    public static Set<HolidayType> fromCommaSeparatedString(String typesString) {
        if (typesString == null || typesString.isEmpty()) {
            return Set.of(PUBLIC);
        }
        return Arrays.stream(typesString.split(",")).map(String::trim).map(HolidayType::fromValue)
                .collect(Collectors.toSet());
    }

    public static String toCommaSeparatedString(Set<HolidayType> types) {
        if (types == null || types.isEmpty()) {
            return PUBLIC.getValue();
        }
        return types.stream().map(HolidayType::getValue).collect(Collectors.joining(","));
    }

    public static HolidayType getDefault() {
        return PUBLIC;
    }

    public static String getDefaultValue() {
        return PUBLIC.value;
    }
}
