package io.npee.holidaykeeper.web.controller;

import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;
import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import io.npee.holidaykeeper.domain.model.holiday.HolidayRegion;
import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import io.npee.holidaykeeper.domain.model.region.Region;
import io.npee.holidaykeeper.domain.repository.HolidayRegionRepository;
import io.npee.holidaykeeper.domain.repository.HolidayRepository;
import io.npee.holidaykeeper.domain.repository.RegionRepository;
import io.npee.holidaykeeper.domain.service.HolidayExternalFetchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HolidayControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    HolidayRegionRepository holidayRegionRepository;

    @MockitoBean
    HolidayExternalFetchService holidayExternalFetchService;

    @BeforeEach
    void cleanUp() {
        holidayRegionRepository.deleteAll();
        holidayRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @Test
    @DisplayName("from/to, countryCode로 공휴일을 페이징 조회한다")
    void searchHolidays_success() throws Exception {

        // given
        Region krRegion = regionRepository.save(
                Region.builder()
                        .countryCode("KR")
                        .regionCode(null)
                        .isoCode("KR")
                        .build()
        );

        Holiday holiday = Holiday.builder()
                .countryCode("KR")
                .date(LocalDate.of(2025, 1, 1))
                .localName("새해 첫날")
                .name("New Year's Day")
                .fixed(true)
                .global(true)
                .launchYear(1949)
                .build();

        holiday.getTypes().add(HolidayType.PUBLIC);

        HolidayRegion holidayRegion = HolidayRegion.builder()
                .holiday(holiday)
                .region(krRegion)
                .build();
        holiday.getHolidayRegions().add(holidayRegion);

        holidayRepository.save(holiday);

        // when & then
        mockMvc.perform(get("/api/holidays")
                        .param("from", "2024")
                        .param("to", "2025")
                        .param("countryCode", "KR")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].countryCode").value("KR"))
                .andExpect(jsonPath("$.content[0].date").value("2025-01-01"))
                .andExpect(jsonPath("$.content[0].localName").value("새해 첫날"))
                .andExpect(jsonPath("$.content[0].types[0]").value("PUBLIC"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("from > to 인 경우 400 에러를 반환한다")
    void searchHolidays_범위_오류() throws Exception {
        // when & then
        mockMvc.perform(get("/api/holidays")
                        .param("from", "2025")
                        .param("to", "2020"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("DELETE /api/holidays: 특정 연도+국가 공휴일을 완전 삭제한다")
    void deleteHolidays_success() throws Exception {
        // given
        Region krRegion = regionRepository.save(
                Region.builder()
                        .countryCode("KR")
                        .regionCode(null)
                        .isoCode("KR")
                        .build()
        );

        // 2025년 KR 공휴일 1건
        Holiday h2025 = Holiday.builder()
                .countryCode("KR")
                .date(LocalDate.of(2025, 1, 1))
                .localName("새해 첫날")
                .name("New Year's Day")
                .fixed(true)
                .global(true)
                .launchYear(1949)
                .build();
        h2025.getTypes().add(HolidayType.PUBLIC);

        HolidayRegion hr2025 = HolidayRegion.builder()
                .holiday(h2025)
                .region(krRegion)
                .build();
        h2025.getHolidayRegions().add(hr2025);

        // 2024년 KR 공휴일 1건 (삭제 대상 아님)
        Holiday h2024 = Holiday.builder()
                .countryCode("KR")
                .date(LocalDate.of(2024, 1, 1))
                .localName("이전 해 첫날")
                .name("New Year's Day 2024")
                .fixed(true)
                .global(true)
                .launchYear(1948)
                .build();
        h2024.getTypes().add(HolidayType.PUBLIC);
        HolidayRegion hr2024 = HolidayRegion.builder()
                .holiday(h2024)
                .region(krRegion)
                .build();
        h2024.getHolidayRegions().add(hr2024);

        holidayRepository.saveAll(List.of(h2024, h2025));

        assertThat(holidayRepository.count()).isEqualTo(2);

        // when & then
        mockMvc.perform(delete("/api/holidays")
                        .param("year", "2025")
                        .param("countryCode", "KR"))
                .andExpect(status().isNoContent());

        List<Holiday> remain = holidayRepository.findAll();
        assertThat(remain).hasSize(1);
        assertThat(remain.getFirst().getDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    @DisplayName("PUT /api/holidays/upsert: 외부 API 기준으로 특정 연도+국가 공휴일을 재적재한다")
    void upsertHolidays_success() throws Exception {
        // given
        Region krRegion = regionRepository.save(
                Region.builder()
                        .countryCode("KR")
                        .regionCode(null)
                        .isoCode("KR")
                        .build()
        );

        // 기존에 있던 2025년 KR 공휴일 (upsert 시 삭제 대상)
        Holiday oldHoliday = Holiday.builder()
                .countryCode("KR")
                .date(LocalDate.of(2025, 1, 1))
                .localName("옛 공휴일")
                .name("Old Holiday")
                .fixed(true)
                .global(false)
                .launchYear(1900)
                .build();
        oldHoliday.getTypes().add(HolidayType.PUBLIC);
        oldHoliday.getHolidayRegions().add(
                HolidayRegion.builder()
                        .holiday(oldHoliday)
                        .region(krRegion)
                        .build()
        );
        holidayRepository.save(oldHoliday);

        assertThat(holidayRepository.count()).isEqualTo(1);

        // 외부 API에서 새로 가져올 공휴일 2건 mock
        ExternalHoliday newYear = ExternalHoliday.builder()
                .countryCode("KR")
                .date("2025-01-01")
                .localName("새해 첫날")
                .name("New Year's Day")
                .types(List.of(HolidayType.PUBLIC))
                .counties(List.of())
                .fixed(true)
                .global(true)
                .launchYear(1949)
                .build();

        ExternalHoliday chuseok = ExternalHoliday.builder()
                .countryCode("KR")
                .date("2025-09-07")
                .localName("추석")
                .name("Chuseok")
                .types(List.of(HolidayType.PUBLIC))
                .counties(List.of())
                .fixed(true)
                .global(false)
                .launchYear(1945)
                .build();

        when(holidayExternalFetchService.fetchHolidaysBy(2025, "KR"))
                .thenReturn(List.of(newYear, chuseok));

        // when & then
        mockMvc.perform(put("/api/holidays/upsert")
                        .param("year", "2025")
                        .param("countryCode", "KR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upsertedCount").value(2));

        List<Holiday> holidays = holidayRepository.findAll();
        assertThat(holidays).hasSize(2);
        assertThat(holidays)
                .extracting(Holiday::getLocalName)
                .containsExactlyInAnyOrder("새해 첫날", "추석");
    }
}