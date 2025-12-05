package io.npee.holidaykeeper.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import io.npee.holidaykeeper.web.controller.dto.HolidaySearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.npee.holidaykeeper.domain.model.holiday.QHoliday.holiday;
import static io.npee.holidaykeeper.domain.model.holiday.QHolidayRegion.holidayRegion;
import static io.npee.holidaykeeper.domain.model.region.QRegion.region;

@Repository
@RequiredArgsConstructor
public class HolidayQueryRepositoryImpl implements HolidayQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Holiday> search(HolidaySearchCondition condition, Pageable pageable) {

        List<Holiday> content = queryFactory.selectFrom(holiday)
                .leftJoin(holiday.holidayRegions, holidayRegion).fetchJoin()
                .leftJoin(holidayRegion.region, region).fetchJoin()
                .where(
                        yearEq(condition.getYear()),
                        countryEq(condition.getCountryCode()),
                        typeEq(condition.getType()),
                        regionIsoEq(condition.getRegionIso())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(holiday.date.desc())
                .fetch();

        Long total = queryFactory.select(holiday.count())
                .from(holiday)
                .leftJoin(holiday.holidayRegions, holidayRegion)
                .leftJoin(holidayRegion.region, region)
                .where(
                        yearEq(condition.getYear()),
                        countryEq(condition.getCountryCode()),
                        typeEq(condition.getType()),
                        regionIsoEq(condition.getRegionIso())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression yearEq(Integer year) {
        return year == null ? null : holiday.date.year().eq(year);
    }

    private BooleanExpression countryEq(String countryCode) {
        return countryCode == null || countryCode.isBlank() ? null : holiday.countryCode.eq(countryCode);
    }

    private BooleanExpression regionIsoEq(String regionIso) {
        return regionIso == null || regionIso.isBlank() ? null : region.isoCode.eq(regionIso);
    }

    private BooleanExpression typeEq(HolidayType type) {
        return type == null ? null : holiday.types.any().eq(type);
    }
}
