package com.planitsquare.holiday_keeper.domain.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "public_holidays",
        indexes = {@Index(name = "idx_country_year", columnList = "country_id,holiday_year"),
                @Index(name = "idx_date", columnList = "date"),
                @Index(name = "idx_type", columnList = "types")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PublicHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "holiday_year", nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String localName;

    @Column(nullable = false, length = 20)
    private String countryCode;

    @Column(nullable = false, length = 50)
    private String types;

    @Column(nullable = false)
    private Boolean fixed;

    @Column(nullable = false)
    private Boolean global;

    @Column(length = 10)
    private String launchYear;

    @Builder
    public PublicHoliday(final Country country, final Integer year, final LocalDate date,
            final String name, final String localName, final String countryCode, final String types,
            final Boolean fixed, final Boolean global, final String launchYear) {
        this.country = country;
        this.year = year;
        this.date = date;
        this.name = name;
        this.localName = localName;
        this.countryCode = countryCode;
        this.types = types;
        this.fixed = fixed;
        this.global = global;
        this.launchYear = launchYear;
    }

    public void updateFrom(final PublicHoliday other) {
        this.name = other.name;
        this.localName = other.localName;
        this.types = other.types;
        this.fixed = other.fixed;
        this.global = other.global;
        this.launchYear = other.launchYear;
    }

    public String getCountryName() {
        return country.getName();
    }
}
