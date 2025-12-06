package io.npee.holidaykeeper.domain.service;

import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;
import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import io.npee.holidaykeeper.domain.model.region.Region;
import io.npee.holidaykeeper.domain.repository.HolidayRepository;
import io.npee.holidaykeeper.domain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayMutationService {

    private final HolidayRepository holidayRepository;
    private final RegionRepository regionRepository;
    private final HolidayExternalFetchService holidayExternalFetchService;

    @Transactional
    public void deleteByYearAndCountry(int year, String countryCode) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);

        List<Holiday> holidays = holidayRepository.findByCountryCodeAndDateBetween(countryCode, from, to);

        if (holidays.isEmpty()) {
            log.info("해당하는 휴일 데이터가 없습니다. countryCode={} in year={}", countryCode, year);
            return;
        }

        holidayRepository.deleteAll(holidays);
        log.info("휴일 데이터 삭제 완료 countryCode={} in year={}, deletedCount={}", countryCode, year, holidays.size());
    }

    @Transactional
    public int upsertByYearAndCountry(int year, String countryCode) {

        deleteByYearAndCountry(year, countryCode);

        List<ExternalHoliday> extHolidays = holidayExternalFetchService.fetchHolidaysBy(year, countryCode);

        if (extHolidays.isEmpty()) {
            log.info("해당하는 외부 휴일 데이터가 없습니다. countryCode={} in year={}", countryCode, year);
            return 0;
        }

        Map<String, Region> regionByIso = regionRepository.findAll().stream()
                .collect(Collectors.toMap(Region::getIsoCode, r -> r));

        List<Holiday> holidays = extHolidays.stream()
                .map(ext -> ext.toEntity(regionByIso))
                .toList();

        holidayRepository.saveAll(holidays);

        int count = holidays.size();
        log.info("휴일 데이터 덮어쓰기 완료 countryCode={} in year={}, upsertedCount={}", countryCode, year, count);

        return count;
    }
}
