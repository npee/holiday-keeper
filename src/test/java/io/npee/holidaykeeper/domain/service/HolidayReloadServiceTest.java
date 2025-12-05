package io.npee.holidaykeeper.domain.service;

import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;
import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import io.npee.holidaykeeper.domain.model.region.Region;
import io.npee.holidaykeeper.domain.repository.HolidayRegionRepository;
import io.npee.holidaykeeper.domain.repository.HolidayRepository;
import io.npee.holidaykeeper.domain.repository.RegionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class HolidayReloadServiceTest {

    private static final Logger log = LoggerFactory.getLogger(HolidayReloadServiceTest.class);
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
        // given - Region은 테스트에서 직접 seed
        regionRepository.save(Region.builder()
                .countryCode("KR")
                .regionCode(null)
                .isoCode("KR")
                .build());
        regionRepository.save(Region.builder()
                .countryCode("US")
                .regionCode("TX")
                .isoCode("US-TX")
                .build());

        // 외부 API mock: KR, US-TX 공휴일 2건 리턴
        ExternalHoliday krHoliday = ExternalHoliday.builder()
                .countryCode("KR")
                .date("2022-10-03")
                .localName("개천절")
                .name("National Foundation Day")
                .types(List.of(HolidayType.PUBLIC))
                .counties(null)
                .build();

        ExternalHoliday usTxHoliday = ExternalHoliday.builder()
                .countryCode("US")
                .date("2022-03-02")
                .localName("텍사스 독립기념일")
                .name("Texas Independence Day")
                .types(List.of(HolidayType.OBSERVANCE))
                .counties(List.of("US-TX"))
                .build();

        when(holidayExternalFetchService.fetchAllHolidaysForRecentFiveYears())
                .thenReturn(List.of(krHoliday, usTxHoliday));

        // when
        holidayReloadService.reloadHolidays();

        // then - Region 3개(KR, US-TX, US), Holiday 2개, HolidayRegion 2개 매핑 저장 확인
        assertThat(regionRepository.count()).isEqualTo(3);
        assertThat(holidayRepository.count()).isEqualTo(2);
        assertThat(holidayRegionRepository.count()).isEqualTo(2);
    }

}