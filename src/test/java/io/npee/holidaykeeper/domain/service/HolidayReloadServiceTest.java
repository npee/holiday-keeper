package io.npee.holidaykeeper.domain.service;

import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;
import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import io.npee.holidaykeeper.domain.model.region.Region;
import io.npee.holidaykeeper.domain.repository.HolidayRegionRepository;
import io.npee.holidaykeeper.domain.repository.HolidayRepository;
import io.npee.holidaykeeper.domain.repository.RegionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@SpringBootTest
class HolidayReloadServiceTest {

    @Autowired
    HolidayReloadService holidayReloadService;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    HolidayRegionRepository holidayRegionRepository;

    @MockitoBean
    HolidayExternalFetchService holidayExternalFetchService;

    @Test
    @DisplayName("reloadHolidays 실행 시 기존 데이터 삭제 후 외부 데이터 기준으로 Holiday/Region 매핑이 저장된다")
    void reloadHolidays_test() {

        // given
        Region kr = regionRepository.save(new Region(null, "KR", null, "KR"));
        Region usTx = regionRepository.save(new Region(null, "US", "TX", "US-TX"));

        // mock
        given(holidayExternalFetchService.fetchAllHolidaysForRecentFiveYears())
                .willReturn(
                        List.of(
                                ExternalHoliday.builder()
                                        .countryCode("KR")
                                        .date("2025-01-01")
                                        .localName("Local A")
                                        .name("A")
                                        .types(List.of(HolidayType.PUBLIC))
                                        .counties(List.of())
                                        .fixed(true)
                                        .global(true)
                                        .launchYear(2000)
                                        .build(),
                                ExternalHoliday.builder()
                                        .countryCode("US")
                                        .date("2025-07-04")
                                        .localName("Local B")
                                        .name("B")
                                        .types(List.of(HolidayType.PUBLIC))
                                        .counties(List.of("US-TX"))
                                        .fixed(true)
                                        .global(false)
                                        .launchYear(2000)
                                        .build()
                        )
                );

        // when
        holidayReloadService.reloadHolidays();

        // then
        assertThat(holidayRepository.count()).isEqualTo(2);
        assertThat(holidayRegionRepository.count()).isEqualTo(2);
    }

}