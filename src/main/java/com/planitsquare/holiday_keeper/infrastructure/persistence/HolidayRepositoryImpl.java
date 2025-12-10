package com.planitsquare.holiday_keeper.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.planitsquare.holiday_keeper.api.dto.request.HolidaySearchRequest;
import com.planitsquare.holiday_keeper.domain.entity.PublicHoliday;
import com.planitsquare.holiday_keeper.domain.entity.QPublicHoliday;
import com.planitsquare.holiday_keeper.domain.repository.HolidayRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepository {

    private final JpaHolidayRepository jpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public PublicHoliday save(final PublicHoliday holiday) {
        return jpaRepository.save(holiday);
    }

    @Override
    public List<PublicHoliday> saveAll(final List<PublicHoliday> holidays) {
        return jpaRepository.saveAll(holidays);
    }

    @Override
    public void deleteAll(final List<PublicHoliday> holidays) {
        jpaRepository.deleteAll(holidays);
    }

    @Override
    public Boolean existsByCountryCodeAndYear(final String countryCode, final Integer year) {
        return jpaRepository.existsByCountryCodeAndYear(countryCode, year);
    }

    @Override
    public List<PublicHoliday> findByCountryCodeAndYear(final String countryCode,
            final Integer year) {
        return jpaRepository.findByCountryCodeAndYear(countryCode, year);
    }

    @Override
    public void deleteByCountryCodeAndYear(final String countryCode, final Integer year) {
        jpaRepository.deleteByCountryCodeAndYear(countryCode, year);
    }

    @Override
    public Page<PublicHoliday> search(final HolidaySearchRequest request, final Pageable pageable) {
        final QPublicHoliday holiday = QPublicHoliday.publicHoliday;
        final BooleanBuilder builder = buildSearchConditions(request, holiday);

        final Long total = countTotalHolidays(holiday, builder);
        final List<PublicHoliday> content = fetchHolidaysWithPaging(holiday, builder, pageable);

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder buildSearchConditions(final HolidaySearchRequest request,
            final QPublicHoliday holiday) {
        final BooleanBuilder builder = new BooleanBuilder();

        addCountryCodeCondition(builder, holiday, request.countryCode());
        addYearCondition(builder, holiday, request.year());
        addDateRangeCondition(builder, holiday, request.from(), request.to());
        addTypesCondition(builder, holiday, request.types());

        return builder;
    }

    private void addCountryCodeCondition(final BooleanBuilder builder, final QPublicHoliday holiday,
            final String countryCode) {
        if (countryCode != null && !countryCode.isEmpty()) {
            builder.and(holiday.countryCode.eq(countryCode));
        }
    }

    private void addYearCondition(final BooleanBuilder builder, final QPublicHoliday holiday,
            final Integer year) {
        if (year != null) {
            builder.and(holiday.year.eq(year));
        }
    }

    private void addDateRangeCondition(final BooleanBuilder builder, final QPublicHoliday holiday,
            final LocalDate from, final LocalDate to) {
        if (from != null) {
            builder.and(holiday.date.goe(from));
        }
        if (to != null) {
            builder.and(holiday.date.loe(to));
        }
    }

    private void addTypesCondition(final BooleanBuilder builder, final QPublicHoliday holiday,
            final String types) {
        if (types != null && !types.isEmpty()) {
            builder.and(holiday.types.contains(types));
        }
    }

    private Long countTotalHolidays(final QPublicHoliday holiday, final BooleanBuilder builder) {
        return queryFactory.select(Expressions.ONE.count()).from(holiday).where(builder).fetchOne();
    }

    private List<PublicHoliday> fetchHolidaysWithPaging(final QPublicHoliday holiday,
            final BooleanBuilder builder, final Pageable pageable) {
        return queryFactory.selectFrom(holiday).innerJoin(holiday.country).fetchJoin()
                .where(builder).orderBy(holiday.date.asc()).offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();
    }
}
