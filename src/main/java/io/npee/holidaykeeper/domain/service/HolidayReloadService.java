package io.npee.holidaykeeper.domain.service;

import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;
import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import io.npee.holidaykeeper.domain.model.region.Region;
import io.npee.holidaykeeper.domain.repository.HolidayRegionRepository;
import io.npee.holidaykeeper.domain.repository.HolidayRepository;
import io.npee.holidaykeeper.domain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayReloadService {

    private final HolidayExternalFetchService holidayExternalFetchService;

    private final HolidayRepository holidayRepository;
    private final HolidayRegionRepository holidayRegionRepository;
    private final RegionRepository regionRepository;

    @Transactional
    public void reloadHolidays() {
        long startTime = System.currentTimeMillis();
        log.info("휴일 데이터 리로드 시작");
        holidayRegionRepository.deleteAllInBatch();
        holidayRepository.deleteAllInBatch();

        log.info("최근 5년간의 휴일 데이터를 가져오는 중...");
        List<ExternalHoliday> fetchHolidays = holidayExternalFetchService.fetchAllHolidaysForRecentFiveYears();
        log.info("휴일 데이터 {}건 조회 완료", fetchHolidays.size());

        Set<String> isoCodes = new HashSet<>();

        for (ExternalHoliday ext : fetchHolidays) {
            isoCodes.add(ext.getCountryCode());

            if (ext.getCounties() != null) {
                isoCodes.addAll(ext.getCounties());
            }
        }

        Map<String, Region> existing = regionRepository.findAll().stream()
                .collect(Collectors.toMap(Region::getIsoCode, r -> r));

        List<Region> newRegions = isoCodes.stream()
                .filter(iso -> !existing.containsKey(iso))
                .map(iso -> {
                    String[] parts = iso.split("-");
                    String countryCode = parts[0];
                    String regionCode = parts.length > 1 ? parts[1] : null;
                    return Region.builder()
                            .countryCode(countryCode)
                            .regionCode(regionCode)
                            .isoCode(iso)
                            .build();
                })
                .toList();

        regionRepository.saveAll(newRegions);

        Map<String, Region> regionByIso = regionRepository.findAll().stream()
                .collect(Collectors.toMap(Region::getIsoCode, r -> r));

        List<Holiday> holidays = new ArrayList<>();

        for (ExternalHoliday extHoliday : fetchHolidays) {
            Holiday holiday = extHoliday.toEntity(regionByIso);
            holidays.add(holiday);
        }

        List<Holiday> savedHolidays = holidayRepository.saveAll(holidays);
        log.info("휴일 데이터 {}건 저장 완료", savedHolidays.size());

        long endTime = System.currentTimeMillis();
        log.info("휴일 데이터 리로드 완료. 소요 시간: {} ms", (endTime - startTime));
    }
}
